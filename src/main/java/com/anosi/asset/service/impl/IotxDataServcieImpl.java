package com.anosi.asset.service.impl;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
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
@PropertySource("classpath:iotx/iotx.properties")
public class IotxDataServcieImpl implements IotxDataService {

	private static final Logger logger = LoggerFactory.getLogger(IotxDataServcieImpl.class);

	@Autowired
	private IotxDataDao iotxDataDao;
	@Autowired
	private IotxDataContentService iotxDataContentService;
	@Value("${sensor.sampling}")
	private double sampling;

	/***
	 * 获取动态线图上的数据
	 * 
	 * @param sensorSN
	 * @param param
	 * @param timeUnit
	 *            时间单位，用来区分月线，周线，日线等，以此来获取需要取出的数据数量
	 *            example:如果要看周线，就将一周时间换算成秒数:7*24*60*60,然后除以取样频率就获得了要获取的row总数
	 * @param sort
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<IotxData> findDynamicData(Predicate predicate, Integer timeUnit, Sort sort) throws Exception {
		logger.debug("findDynamicData");
		// TODO 从数据库中读取这个数据的发送频率，现在为了测试直接默认1秒
		Iterable<IotxData> findDynamicData = this.iotxDataDao.findAll(predicate,
				new PageRequest(0, (int) Math.round(timeUnit / sampling), sort));

		List<IotxData> iotxDatas = Lists.newArrayList(findDynamicData);
		Collections.reverse(iotxDatas);
		return iotxDatas;
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
	public Iterable<IotxData> save(Iterable<IotxData> iotxDatas) {
		return iotxDataDao.save(iotxDatas);
	}

	@Override
	public Page<IotxData> findByContentSearch(String content, Predicate predicate, Pageable pageable) {
		Account account = SessionUtil.getCurrentUser();
		Page<IotxDataContent> iotxDataContents;
		// 防止sort报错，只获取pageable的页数和size
		logger.debug("page:{},size:{}",pageable.getPageNumber(), pageable.getPageSize());
		Pageable contentPage = new PageRequest(pageable.getPageNumber(), pageable.getPageSize());
		if (account.isAdmin()) {
			iotxDataContents = iotxDataContentService.findByContent(content, contentPage);
		} else {
			iotxDataContents = iotxDataContentService.findByContent(account.getCompany().getName(), content, contentPage);
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
		logger.debug("page:{},size:{}",pageable.getPageNumber(), pageable.getPageSize());
		Pageable contentPage = new PageRequest(pageable.getPageNumber(), pageable.getPageSize());
		if (account.isAdmin()) {
			iotxDataContents = iotxDataContentService.findByContentAndAlarm(content, isAlarm, contentPage);
		} else {
			iotxDataContents = iotxDataContentService.findByContentAndAlarmAndCompanyName(content, isAlarm, account.getCompany().getName(), contentPage);
		}
		List<BigInteger> ids = iotxDataContents.getContent().stream().map(c -> new BigInteger(c.getId()))
				.collect(Collectors.toList());
		return findAll(QIotxData.iotxData.id.in(ids).and(predicate), pageable);
	}

}
