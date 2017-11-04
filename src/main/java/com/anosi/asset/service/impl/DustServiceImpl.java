package com.anosi.asset.service.impl;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.component.I18nComponent;
import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.DustDao;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.Dust;
import com.anosi.asset.mqtt.MqttServer;
import com.anosi.asset.service.DustService;
import com.google.common.collect.ImmutableMap;

@Service("dustService")
@Transactional
public class DustServiceImpl extends BaseJPAServiceImpl<Dust> implements DustService {

	private static final Logger logger = LoggerFactory.getLogger(DustServiceImpl.class);

	@Autowired
	private DustDao dustDao;
	@Autowired
	private I18nComponent i18nComponent;
	@Autowired
	private MqttServer mqttServer;
	@Autowired
	private EntityManager entityManager;

	@Override
	public BaseJPADao<Dust> getRepository() {
		return dustDao;
	}

	@Override
	public Page<Dust> findByContentSearch(String content, Pageable pageable) {
		Account account = sessionComponent.getCurrentUser();
		logger.debug("page:{},size:{}", pageable.getPageNumber(), pageable.getPageSize());
		if (account.isAdmin()) {
			return dustDao.findBySearchContent(entityManager, content, pageable);
		} else {
			return dustDao.findBySearchContent(entityManager, content, pageable, account.getCompany().getName());
		}
	}

	@Override
	public Dust findBySerialNo(String serialNo) {
		return dustDao.findBySerialNo(serialNo);
	}

	@Override
	public void remoteUpdate(Dust dust, String name, Double frequency, boolean isWorked) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("header", new JSONObject(ImmutableMap.of("uniqueId", UUID.randomUUID().toString(), "type",
				"dust", "serialNo", dust.getSerialNo())));
		if (!Objects.equals(dust.getName(), name)) {
			sendMessage(dust, setBody("name", name, jsonObject));
		}
		if (!Objects.equals(dust.getFrequency(), frequency)) {
			sendMessage(dust, setBody("frequency", frequency, jsonObject));
		}
		if (!Objects.equals(dust.getIsWorked(), isWorked)) {
			sendMessage(dust, setBody("isWorked", isWorked, jsonObject));
		}
	}
	
	private JSONObject setBody(String type, Object val, JSONObject jsonObject) {
		JSONObject bodyJson = new JSONObject();
		bodyJson.put("type", type);
		bodyJson.put("val", val);
		jsonObject.put("body", bodyJson);
		return jsonObject;
	}
	
	private void sendMessage(Dust dust,JSONObject jsonObject){
		MqttMessage message = new MqttMessage();
		message.setQos(2);
		message.setRetained(true);
		message.setPayload(jsonObject.toString().getBytes());
		try {
			mqttServer.publish("configure/" + dust.getIotx().getSerialNo(), message);
		} catch (MqttException e) {
			e.printStackTrace();
			throw new CustomRunTimeException(i18nComponent.getMessage("mqtt.message.send.fail"));
		}
	}

}
