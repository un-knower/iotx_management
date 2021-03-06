package com.anosi.asset.kafka;

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
import com.anosi.asset.model.mongo.IotxData;
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

	private ValueOperations<String, String> opv;

	private String lastValue;

	private Lock lock = new ReentrantLock();// 锁对象

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
	 */
	private void handleIotxData(String content) {
		IotxData iotxData = JSON.parseObject(content, IotxData.class);
		// 进行连续性比对
		//checkIsContinuously(iotxData.getSensorSN() + "ForIotxDataContinuous", checkValue(iotxData));
		iotxDataService.save(iotxData);
	}

	/***
	 * 处理val,判断val是否正常
	 * 
	 * @param iotxData
	 * @return
	 */
	/*private String checkValue(IotxData iotxData) {
		Double val = iotxData.getVal();
		Double maxVal = iotxData.getMaxVal();
		Double minVal = iotxData.getMinVal();

		String message = null;
		Level level = null;

		if (val < maxVal && val > minVal) {
			level = Level.NORMAL;
		} else if (val > maxVal) {
			level = Level.ALARM_1;
			iotxData.setAlarm(true);
			iotxData.setMessage(message);
		} else if (val < minVal) {
			level = Level.ALARM_1;
			iotxData.setAlarm(true);
			iotxData.setMessage(message);
		}

		iotxData.setLevel(level);
		return level.getLevel();
	}*/

	/***
	 * 判断是否同侧连续告警
	 * 
	 * @param key
	 * @param value
	 * 
	 *            考虑多线程安全,加入了锁,防止出现在比对还没完成前,另一个线程完成了比对
	 */
	private void checkIsContinuously(String key, String value) {
		lock.lock();// 得到锁
		try {
			lastValue = opv.get(key);
			// 如果两次值不相等
			if (!Objects.equals(value, lastValue)) {
				opv.set(key, value);
			}
		} finally {
			lock.unlock();// 释放锁
		}
	}

}
