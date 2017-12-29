package com.anosi.asset.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.cache.annotation.sensor.SensorEvictCache;
import com.anosi.asset.cache.annotation.sensor.SensorSaveCache;
import com.anosi.asset.component.I18nComponent;
import com.anosi.asset.component.SessionComponent;
import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.SensorDao;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.Dust;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.Sensor;
import com.anosi.asset.model.jpa.Sensor.DataType;
import com.anosi.asset.model.mongo.IotxData;
import com.anosi.asset.model.mongo.Message;
import com.anosi.asset.mqtt.MqttServer;
import com.anosi.asset.service.DustService;
import com.anosi.asset.service.IotxDataService;
import com.anosi.asset.service.IotxService;
import com.anosi.asset.service.SensorService;
import com.anosi.asset.util.BeanRefUtil;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.Predicate;

@Service("sensorService")
@CacheConfig(cacheNames = "sensor")
public class SensorServiceImpl extends BaseJPAServiceImpl<Sensor> implements SensorService {

	private static final Logger logger = LoggerFactory.getLogger(SensorServiceImpl.class);

	@Autowired
	private SensorDao sensorDao;
	@Autowired
	private I18nComponent i18nComponent;
	@Autowired
	private MqttServer mqttServer;
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private IotxService iotxService;
	@Autowired
	private IotxDataService iotxDataService;
	@Autowired
	private DustService dustService;
	@Autowired
	private SensorCacheComponent sensorCacheComponent;

	@Override
	public Page<Sensor> findAll(Predicate predicate, Pageable pageable) {
		logger.debug("finAll by predicate:{}", predicate == null ? "is null" : predicate.toString());
		Page<Sensor> sensorPage = sensorDao.findAll(predicate, pageable);
		return sensorPage;
	}

	@Override
	public BaseJPADao<Sensor> getRepository() {
		return sensorDao;
	}

	@Override
	@SensorEvictCache // 清理缓存
	@SensorSaveCache
	@Transactional
	public <S extends Sensor> S save(S sensor) {
		return super.save(sensor);
	}

	@Override
	@Transactional
	public <S extends Sensor> Iterable<S> save(Iterable<S> sensors) {
		// 需要挨个清理缓存
		sensors = super.save(sensors);
		sensors.forEach(sensor -> sensorCacheComponent.refresh(sensor));
		return sensors;
	}

	@Override
	@SensorEvictCache // 清理缓存
	@Transactional
	public void delete(Sensor sensor) {
		sensorDao.delete(sensor);
	}

	@Override
	@Cacheable(key = "#serialNo+'ForSensor'")
	public Sensor findBySerialNo(String serialNo) {
		return sensorDao.findBySerialNoEquals(serialNo);
	}

	@Override
	public Page<Sensor> findByContentSearch(String content, Pageable pageable, Long iotxId) {
		Account account = sessionComponent.getCurrentUser();
		logger.debug("page:{},size:{}", pageable.getPageNumber(), pageable.getPageSize());
		String iotxSN = null;
		if (iotxId != null) {
			iotxSN = iotxService.getOne(iotxId).getSerialNo();
		}
		if (SessionComponent.isAdmin()) {
			return sensorDao.findBySearchContent(entityManager, content, pageable, iotxSN);
		} else {
			return sensorDao.findBySearchContent(entityManager, content, pageable, account.getCompany().getCode(),
					iotxSN);
		}
	}

	@Override
	public void remoteUpdate(Sensor sensor, boolean isWorked, Double frequency) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("header", new JSONObject(ImmutableMap.of("uniqueId", UUID.randomUUID().toString(), "type",
				"sensor", "serialNo", sensor.getSerialNo())));
		if (!Objects.equals(sensor.getIsWorked(), isWorked)) {
			sendMessage(sensor, setBody("is_used", isWorked, jsonObject));
		}
		if (!Objects.equals(sensor.getDust().getFrequency(), frequency)) {
			sendMessage(sensor, setBody("job_time", frequency, jsonObject));
		}
	}

	private JSONObject setBody(String type, Object val, JSONObject jsonObject) {
		JSONObject bodyJson = new JSONObject();
		bodyJson.put("type", type);
		bodyJson.put("value", val);
		jsonObject.put("body", bodyJson);
		return jsonObject;
	}

	private void sendMessage(Sensor sensor, JSONObject jsonObject) {
		MqttMessage message = new MqttMessage();
		message.setQos(2);
		message.setRetained(true);
		message.setPayload(jsonObject.toString().getBytes());
		try {
			mqttServer.publish("configure/" + sensor.getDust().getIotx().getSerialNo(), message);
		} catch (MqttException e) {
			e.printStackTrace();
			throw new CustomRunTimeException(i18nComponent.getMessage("mqtt.message.send.fail"));
		}
	}

	@Override
	@Deprecated
	public Page<Sensor> setActualValue(Page<Sensor> sensors) {
		PageRequest pageRequest = new PageRequest(0, 1, new Sort(Direction.DESC, "collectTime"));// 只取一条
		sensors.getContent().forEach(sensor -> {
			List<IotxData> iotxDatas = iotxDataService.findBySensorSN(sensor.getSerialNo(), pageRequest).getContent();
			if (!CollectionUtils.isEmpty(iotxDatas)) {
				Double actualVal = iotxDatas.get(0).getVal();
				logger.debug("sensor:{},actualVal:{}", sensor.getSerialNo(), actualVal);
				sensor.setActualValue(actualVal);
			}
		});
		return sensors;
	}

	@Override
	@Transactional
	public Sensor convertSensor(Message payLoad, Map<String, Object> values) {
		String serialNo = payLoad.getHeader().getSerialNo();
		Sensor sensor = sensorDao.findBySerialNoEquals(serialNo);
		if (sensor == null) {
			sensor = new Sensor();
			// 为sensor创建虚拟的dust
			Dust inventedDust = new Dust();
			// serialNo规定为iotxSN_sensor地址
			Iotx singleIotx = iotxService.findBySerialNo(serialNo.split("_")[0]);
			inventedDust.setSerialNo(UUID.randomUUID().toString());
			inventedDust.setIotx(singleIotx);
			logger.debug("deviceSN:{}", singleIotx.getDevice().getSerialNo());
			inventedDust.setDevice(singleIotx.getDevice());
			inventedDust = dustService.save(inventedDust);
			sensor.setDust(inventedDust);
		}
		try {
			BeanRefUtil.setValue(sensor, values);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomRunTimeException(e.getMessage());
		}
		if (values.containsKey("job_time")) {
			sensor.getDust().setFrequency(((Number) values.get("job_time")).doubleValue());
		}
		if (values.containsKey("type")) {
			sensor.setDataType(DataType.valueOf(values.get("type").toString().toUpperCase()));
		}
		return sensor;
	}

	@Override
	@Transactional
	public Sensor convertSensor(Message payLoad) {
		return convertSensor(payLoad, new HashMap<>(JSON.parseObject(JSON.toJSONString(payLoad.getBody().getVal()))));
	}

	@Override
	public List<Sensor> findByDeviceSN(String deviceSN) {
		return sensorDao.findByDust_device_serialNo(deviceSN);
	}

	@Override
	public List<String> findSerialNoByDevice(String deviceSN) {
		return sensorDao.findSNByDevice(deviceSN);
	}

	/**
	 * 清理sensor缓存的辅助类,专门开一个类是为了aop
	 * 
	 * @author jinyao
	 *
	 */
	@Component
	public static class SensorCacheComponent {

		@SensorEvictCache // 清理缓存
		@SensorSaveCache
		@Transactional
		public Sensor refresh(Sensor sensor) {
			return sensor;
		}

	}

}
