package com.anosi.asset.service.impl;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.component.SessionUtil;
import com.anosi.asset.dao.mongo.IotxDataDao;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.elasticsearch.IotxDataContent;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.mongo.IotxData;
import com.anosi.asset.model.mongo.QIotxData;
import com.anosi.asset.service.IotxDataContentService;
import com.anosi.asset.service.IotxDataService;
import com.google.common.collect.Lists;
import com.querydsl.core.types.Predicate;

@Service("iotxDataService")
@Transactional
@Configuration
public class IotxDataServcieImpl implements IotxDataService {

	private static final Logger logger = LoggerFactory.getLogger(IotxDataServcieImpl.class);

	@Autowired
	private IotxDataDao iotxDataDao;
	@Autowired
	private IotxDataContentService iotxDataContentService;

	@Override
	public Page<IotxData> findDynamicData(Predicate predicate, Double frequency, Integer timeUnit, Sort sort)
			throws Exception {
		logger.debug("findDynamicData");
		PageRequest pageRequest = new PageRequest(0, (int) Math.round(timeUnit / frequency), sort);
		Page<IotxData> findDynamicData = this.iotxDataDao.findAll(predicate, pageRequest);

		List<IotxData> iotxDatas = Lists.newArrayList(findDynamicData.getContent());
		Collections.reverse(iotxDatas);
		return new PageImpl<>(iotxDatas, pageRequest, findDynamicData.getTotalElements());
	}

	@Override
	public Page<IotxData> findAll(Predicate predicate, Pageable pageable) {
		logger.debug("finAll by predicate:{}", predicate == null ? "is null" : predicate.toString());
		return iotxDataDao.findAll(predicate, pageable);
	}

	@Override
	public Long countBysensorSN(String sensorSN) {
		return iotxDataDao.countBysensorSNEquals(sensorSN);
	}

	@Override
	public Long countByiotxSN(String iotxSN) {
		return iotxDataDao.countByiotxSNEquals(iotxSN);
	}

	@Override
	public IotxData save(IotxData iotxData) {
		iotxData = iotxDataDao.save(iotxData);
		try {
			iotxDataContentService.save(iotxData);
		} catch (Exception e) {
			throw new CustomRunTimeException(e.getMessage());
		}
		return iotxData;
	}

	@Override
	public <S extends IotxData> Iterable<S> save(Iterable<S> iotxDatas) {
		iotxDatas = iotxDataDao.save(iotxDatas);
		try {
			iotxDataContentService.save(iotxDatas);
		} catch (Exception e) {
			throw new CustomRunTimeException(e.getMessage());
		}
		return iotxDatas;
	}

	@Override
	public Page<IotxData> findByContentSearch(String content, Predicate predicate, Pageable pageable) {
		Account account = SessionUtil.getCurrentUser();
		Page<IotxDataContent> iotxDataContents;
		// 防止sort报错，只获取pageable的页数和size
		logger.debug("page:{},size:{}", pageable.getPageNumber(), pageable.getPageSize());
		Pageable contentPage = new PageRequest(pageable.getPageNumber(), pageable.getPageSize());
		if (account.isAdmin()) {
			iotxDataContents = iotxDataContentService.findByContent(content, contentPage);
		} else {
			iotxDataContents = iotxDataContentService.findByContent(account.getCompany().getName(), content,
					contentPage);
		}
		List<BigInteger> ids = iotxDataContents.getContent().stream().map(c -> new BigInteger(c.getId()))
				.collect(Collectors.toList());
		return findAll(QIotxData.iotxData.id.in(ids).and(predicate), pageable);
	}

	@Override
	public Page<IotxData> findByContentSearch(String content, Boolean isAlarm, Predicate predicate, Pageable pageable) {
		Account account = SessionUtil.getCurrentUser();
		Page<IotxDataContent> iotxDataContents;
		// 防止sort报错，只获取pageable的页数和size
		logger.debug("page:{},size:{}", pageable.getPageNumber(), pageable.getPageSize());
		Pageable contentPage = new PageRequest(pageable.getPageNumber(), pageable.getPageSize());
		if (account.isAdmin()) {
			iotxDataContents = iotxDataContentService.findByContentAndAlarm(content, isAlarm, contentPage);
		} else {
			iotxDataContents = iotxDataContentService.findByContentAndAlarmAndCompanyName(content, isAlarm,
					account.getCompany().getName(), contentPage);
		}
		List<BigInteger> ids = iotxDataContents.getContent().stream().map(c -> new BigInteger(c.getId()))
				.collect(Collectors.toList());
		return findAll(QIotxData.iotxData.id.in(ids).and(predicate), pageable);
	}

	@Override
	public IotxData findOne(BigInteger id) {
		return iotxDataDao.findOne(id);
	}

}
