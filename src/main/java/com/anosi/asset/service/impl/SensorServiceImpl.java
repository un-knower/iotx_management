package com.anosi.asset.service.impl;

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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.cache.annotation.SensorEvictCache;
import com.anosi.asset.component.I18nComponent;
import com.anosi.asset.component.SessionComponent;
import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.SensorDao;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.Sensor;
import com.anosi.asset.mqtt.MqttServer;
import com.anosi.asset.service.IotxDataService;
import com.anosi.asset.service.SensorService;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.Predicate;

@Service("sensorService")
@Transactional
@CacheConfig(cacheNames = "sensor")
public class SensorServiceImpl extends BaseJPAServiceImpl<Sensor> implements SensorService {

	private static final Logger logger = LoggerFactory.getLogger(SensorServiceImpl.class);

	@Autowired
	private SensorDao sensorDao;
	@Autowired
	private IotxDataService iotxDataService;
	@Autowired
	private I18nComponent i18nComponent;
	@Autowired
	private MqttServer mqttServer;
	@Autowired
	private EntityManager entityManager;

	@Override
	public Page<Sensor> findAll(Predicate predicate, Pageable pageable) {
		logger.debug("finAll by predicate:{}", predicate == null ? "is null" : predicate.toString());
		Page<Sensor> sensorPage = sensorDao.findAll(predicate, pageable);
		// 为每个传感器设置告警数量
		for (Sensor sensor : sensorPage) {
			sensor.setAlarmQuantity(iotxDataService.countBysensorSN(sensor.getSerialNo()));
		}
		return sensorPage;
	}

	@Override
	public BaseJPADao<Sensor> getRepository() {
		return sensorDao;
	}

	@Override
	@SensorEvictCache // 清理缓存
	public void delete(Sensor sensor) {
		sensorDao.delete(sensor);
	}

	@Override
	@Cacheable(key = "#serialNo+'ForSensor'")
	public Sensor findBySerialNo(String serialNo) {
		return sensorDao.findBySerialNoEquals(serialNo);
	}

	@Override
	public Page<Sensor> findByContentSearch(String content, Pageable pageable) {
		Account account = sessionComponent.getCurrentUser();
		logger.debug("page:{},size:{}", pageable.getPageNumber(), pageable.getPageSize());
		if (SessionComponent.isAdmin()) {
			return sensorDao.findBySearchContent(entityManager, content, pageable);
		} else {
			return sensorDao.findBySearchContent(entityManager, content, pageable, account.getCompany().getName());
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

}
