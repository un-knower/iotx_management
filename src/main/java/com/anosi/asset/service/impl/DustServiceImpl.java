package com.anosi.asset.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.component.I18nComponent;
import com.anosi.asset.component.SessionUtil;
import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.DustDao;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.elasticsearch.DustContent;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.Dust;
import com.anosi.asset.model.jpa.QDust;
import com.anosi.asset.mqtt.MqttServer;
import com.anosi.asset.service.DustContentService;
import com.anosi.asset.service.DustService;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.Predicate;

@Service("dustService")
@Transactional
public class DustServiceImpl extends BaseServiceImpl<Dust> implements DustService {

	private static final Logger logger = LoggerFactory.getLogger(DustServiceImpl.class);

	@Autowired
	private DustDao dustDao;
	@Autowired
	private DustContentService dustContentService;
	@Autowired
	private I18nComponent i18nComponent;
	@Autowired
	private MqttServer mqttServer;

	@Override
	public BaseJPADao<Dust> getRepository() {
		return dustDao;
	}

	/***
	 * 重写save,保存dust的同时，将@Content标记的字段内容提取，存储到dustContent中
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Dust save(Dust dust) {
		dustDao.save(dust);

		try {
			dustContentService.save(dust);
		} catch (Exception e) {
			throw new CustomRunTimeException(e.getMessage());
		}
		return dust;
	}

	/***
	 * 重写批量添加
	 * 
	 */
	@Override
	public <S extends Dust> Iterable<S> save(Iterable<S> dusts) {
		dusts = dustDao.save(dusts);

		try {
			dustContentService.save(dusts);
		} catch (Exception e) {
			throw new CustomRunTimeException(e.getMessage());
		}
		return dusts;
	}

	@Override
	public Page<Dust> findByContentSearch(String content, Predicate predicate, Pageable pageable) {
		Account account = SessionUtil.getCurrentUser();
		Page<DustContent> dustContents;
		// 防止sort报错，只获取pageable的页数和size
		logger.debug("page:{},size:{}", pageable.getPageNumber(), pageable.getPageSize());
		Pageable contentPage = new PageRequest(pageable.getPageNumber(), pageable.getPageSize());
		if (account.isAdmin()) {
			dustContents = dustContentService.findByContent(content, contentPage);
		} else {
			dustContents = dustContentService.findByContent(account.getCompany().getName(), content, contentPage);
		}
		List<Long> ids = dustContents.getContent().stream().map(c -> Long.parseLong(c.getId()))
				.collect(Collectors.toList());
		return findAll(QDust.dust.id.in(ids).and(predicate), pageable);
	}

	@Override
	public Dust findBySerialNo(String serialNo) {
		return dustDao.findBySerialNo(serialNo);
	}

	@Override
	public void remoteUpdate(Dust dust, String name, Double frequency, boolean isWorked) {
		JSONObject bodyJson = new JSONObject();
		if (!Objects.equals(dust.getName(), name)) {
			bodyJson.put("name", name);
		}
		if (!Objects.equals(dust.getFrequency(), frequency)) {
			bodyJson.put("frequency", frequency);
		}
		if (!Objects.equals(dust.getIsWorked(), isWorked)) {
			bodyJson.put("isWorked", isWorked);
		}
		if (!bodyJson.isEmpty()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("header", new JSONObject(ImmutableMap.of("type", "dust", "serialNo", dust.getSerialNo())));
			jsonObject.put("body", bodyJson);
			MqttMessage message = new MqttMessage();
			message.setQos(2);
			message.setRetained(true);
			message.setPayload(jsonObject.toString().getBytes());
			try {
				mqttServer.publish("/configure/" + dust.getIotx().getSerialNo(), message);
			} catch (MqttException e) {
				e.printStackTrace();
				throw new CustomRunTimeException(i18nComponent.getMessage("mqtt.message.send.fail"));
			}
		}
	}

}
