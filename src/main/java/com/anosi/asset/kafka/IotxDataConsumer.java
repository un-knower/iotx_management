package com.anosi.asset.kafka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.jpa.AlarmData;
import com.anosi.asset.model.jpa.AlarmData.Level;
import com.anosi.asset.model.jpa.Sensor;
import com.anosi.asset.model.mongo.IotxData;
import com.anosi.asset.service.AlarmDataService;
import com.anosi.asset.service.IotxDataService;
import com.anosi.asset.service.SensorService;

@Component
@Transactional
public class IotxDataConsumer {

	private static final Logger logger = LoggerFactory.getLogger(IotxDataConsumer.class);

	@Autowired
	private IotxDataService iotxDataService;
	@Autowired
	private SensorService sensorService;
	@Autowired
	private AlarmDataService alarmDataService;

	private ValueOperations<String, String> opv;

	private String lastValue;

	private Lock continuouslyLock = new ReentrantLock();// 锁对象

	public IotxDataConsumer(@Autowired RedisTemplate<String, String> redisTemplate) {
		opv = redisTemplate.opsForValue();
	}

	/***
	 * 消费kafka中的数据
	 * 
	 * @param content
	 */
	@KafkaListener(topics = { "${spring.kafka.template.default-topic}" })
	public void processMessage(ConsumerRecord<?, ?> data,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
			@Header(KafkaHeaders.OFFSET) List<Long> offsets) {
		logger.debug("partitions：{},offsets:{},value:{}", partitions, offsets, data.value());
		this.handleIotxData(String.valueOf(data.value()));
	}

	/***
	 * 数据处理入口
	 * 
	 * @param content
	 *            格式:{ "data": [ { "collect_time": 1510624541, "sensorNo":
	 *            "2017010101_40020", "param": "testParam", "val": "44" }, {
	 *            "collect_time": 1510624541, "sensorNo": "2017010101_40020",
	 *            "param": "testParam", "val": "44" } ], "uniqueid":
	 *            "cbb297c0-14a9-46bc-ad91-1d0ef9b42df9" }
	 */
	private void handleIotxData(String content) {
		JSONObject jsonObject = JSON.parseObject(content);
		JSONArray dataArray = jsonObject.getJSONArray("data");
		List<IotxData> iotxDatas = new ArrayList<>();

		for (Object data : dataArray) {
			JSONObject jsonData = (JSONObject) data;
			String sensorSN = jsonData.getString("sensorNo");// 序列号
			Double val = jsonData.getDouble("val");// 采集的数值
			Long collect_time = jsonData.getLong("collect_time");// 采集时间

			IotxData iotxData = new IotxData(sensorSN, val, new Date(collect_time * 1000));
			checkValue(iotxData);

			iotxDatas.add(iotxData);
		}
		Collections.reverse(iotxDatas);
		iotxDataService.save(iotxDatas);
	}

	/***
	 * 处理val,判断val是否正常
	 * 
	 * @param iotxData
	 * @return
	 */
	private void checkValue(IotxData iotxData) {
		Double val = iotxData.getVal();
		Sensor sensor = sensorService.findBySerialNo(iotxData.getSensorSN());
		Double maxVal = sensor.getMaxVal();
		Double minVal = sensor.getMinVal();

		AlarmData alarmData = null;
		String alarmMessage = Level.NORMAL.toString();
		if (maxVal != null && val > maxVal) {
			alarmData = new AlarmData(sensor, iotxData.getVal(), iotxData.getCollectTime(), Level.ALARM_1);
			alarmMessage = Level.ALARM_1.toString() + "-up";
		} else if (minVal != null && val < minVal) {
			alarmData = new AlarmData(sensor, iotxData.getVal(), iotxData.getCollectTime(), Level.ALARM_1);
			alarmMessage = Level.ALARM_1.toString() + "-down";
		}

		// 进行连续告警比对
		boolean isContinuously = checkIsContinuously(iotxData.getSensorSN() + "ForIotxDataContinuous", alarmMessage);
		if (!isContinuously && alarmData != null) {
			alarmDataService.save(alarmData);
		}

	}

	/***
	 * 判断是否同侧连续告警
	 * 
	 * @param key
	 * @param value
	 * 
	 *            考虑多线程安全,加入了锁,防止出现在比对还没完成前,另一个线程完成了比对
	 */
	private boolean checkIsContinuously(String key, String value) {
		continuouslyLock.lock();// 得到锁
		try {
			lastValue = opv.get(key);
			// 如果两次值不相等
			if (!Objects.equals(value, lastValue)) {
				opv.set(key, value);
				return false;// 不连续
			}
			return true;// 连续
		} finally {
			continuouslyLock.unlock();// 释放锁
		}
	}

}
