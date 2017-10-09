package com.anosi.asset.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.cache.annotation.SensorEvictCache;
import com.anosi.asset.cache.annotation.SensorSaveCache;
import com.anosi.asset.component.I18nComponent;
import com.anosi.asset.component.SessionUtil;
import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.SensorDao;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.elasticsearch.SensorContent;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.QSensor;
import com.anosi.asset.model.jpa.Sensor;
import com.anosi.asset.mqtt.MqttServer;
import com.anosi.asset.service.IotxDataService;
import com.anosi.asset.service.SensorContentService;
import com.anosi.asset.service.SensorService;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.Predicate;

@Service("sensorService")
@Transactional
@CacheConfig(cacheNames = "sensor")
public class SensorServiceImpl extends BaseServiceImpl<Sensor> implements SensorService {

	private static final Logger logger = LoggerFactory.getLogger(SensorServiceImpl.class);

	@Autowired
	private SensorDao sensorDao;
	@Autowired
	private IotxDataService iotxDataService;
	@Autowired
	private SensorContentService sensorContentService;
	@Autowired
	private I18nComponent i18nComponent;
	@Autowired
	private MqttServer mqttServer;

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

	@SuppressWarnings("unchecked")
	@Override
	@SensorSaveCache // 缓存
	public Sensor save(Sensor sensor) {
		sensor = sensorDao.save(sensor);

		try {
			sensorContentService.save(sensor);
		} catch (Exception e) {
			throw new CustomRunTimeException(e.getMessage());
		}
		return sensor;
	}

	/***
	 * 重写批量添加
	 * 
	 */
	@Override
	public <S extends Sensor> Iterable<S> save(Iterable<S> sensors) {
		sensors = sensorDao.save(sensors);

		try {
			sensorContentService.save(sensors);
		} catch (Exception e) {
			throw new CustomRunTimeException(e.getMessage());
		}
		return super.save(sensors);
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
	public Page<Sensor> findByContentSearch(String content, Predicate predicate, Pageable pageable) {
		Account account = SessionUtil.getCurrentUser();
		Page<SensorContent> sensorContents;
		// 防止sort报错，只获取pageable的页数和size
		logger.debug("page:{},size:{}", pageable.getPageNumber(), pageable.getPageSize());
		Pageable contentPage = new PageRequest(pageable.getPageNumber(), pageable.getPageSize());
		if (account.isAdmin()) {
			sensorContents = sensorContentService.findByContent(content, contentPage);
		} else {
			sensorContents = sensorContentService.findByContent(account.getCompany().getName(), content, contentPage);
		}
		List<Long> ids = sensorContents.getContent().stream().map(c -> Long.parseLong(c.getId()))
				.collect(Collectors.toList());
		return findAll(QSensor.sensor.id.in(ids).and(predicate), pageable);
	}

	@Override
	public void remoteUpdate(Sensor sensor, boolean isWorked) {
		JSONObject bodyJson = new JSONObject();
		if (!Objects.equals(sensor.getIsWorked(), isWorked)) {
			bodyJson.put("isWorked", isWorked);
		}
		if (!bodyJson.isEmpty()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("header",
					new JSONObject(ImmutableMap.of("type", "sensor", "serialNo", sensor.getSerialNo())));
			jsonObject.put("body", bodyJson);
			MqttMessage message = new MqttMessage();
			message.setQos(2);
			message.setRetained(true);
			message.setPayload(jsonObject.toString().getBytes());
			try {
				mqttServer.publish("/configure/" + sensor.getDust().getIotx().getSerialNo(), message);
			} catch (MqttException e) {
				e.printStackTrace();
				throw new CustomRunTimeException(i18nComponent.getMessage("mqtt.message.send.fail"));
			}
		}
	}

}
