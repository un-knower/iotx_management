package com.anosi.asset.service.impl;

import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.component.SessionComponent;
import com.anosi.asset.dao.mongo.BaseMongoDao;
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
public class IotxDataServcieImpl extends BaseMongoServiceImpl<IotxData> implements IotxDataService {

	private static final Logger logger = LoggerFactory.getLogger(IotxDataServcieImpl.class);

	@Autowired
	private IotxDataDao iotxDataDao;
	@Autowired
	private IotxDataContentService iotxDataContentService;
	@Autowired
	private SessionComponent sessionComponent;
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public BaseMongoDao<IotxData> getRepository() {
		return iotxDataDao;
	}

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
	public <S extends IotxData> S save(S iotxData) {
		iotxData = iotxDataDao.save(iotxData);
		try {
			iotxDataContentService.saveContent(iotxData);
		} catch (Exception e) {
			throw new CustomRunTimeException(e.getMessage());
		}
		return iotxData;
	}

	@Override
	public <S extends IotxData> List<S> save(Iterable<S> iotxDatas) {
		List<S> iotxDataList = iotxDataDao.save(iotxDatas);
		try {
			iotxDataContentService.saveContent(iotxDataList);
		} catch (Exception e) {
			throw new CustomRunTimeException(e.getMessage());
		}
		return iotxDataList;
	}

	@Override
	public Page<IotxData> findByContentSearch(String content, Pageable pageable) {
		Account account = sessionComponent.getCurrentUser();
		Page<IotxDataContent> iotxDataContents;
		// 防止sort报错，只获取pageable的页数和size
		logger.debug("page:{},size:{}", pageable.getPageNumber(), pageable.getPageSize());
		Pageable contentPage = new PageRequest(pageable.getPageNumber(), pageable.getPageSize());
		if (SessionComponent.isAdmin()) {
			iotxDataContents = iotxDataContentService.findByContent(content, contentPage);
		} else {
			iotxDataContents = iotxDataContentService.findByContent(account.getCompany().getName(), content,
					contentPage);
		}
		List<BigInteger> ids = iotxDataContents.getContent().stream().map(c -> new BigInteger(c.getId()))
				.collect(Collectors.toList());
		return findAll(QIotxData.iotxData.id.in(ids), contentPage);
	}

	@Override
	public Page<IotxData> findByContentSearch(String content, Boolean isAlarm, Pageable pageable) {
		Account account = sessionComponent.getCurrentUser();
		Page<IotxDataContent> iotxDataContents;
		// 防止sort报错，只获取pageable的页数和size
		logger.debug("page:{},size:{}", pageable.getPageNumber(), pageable.getPageSize());
		Pageable contentPage = new PageRequest(pageable.getPageNumber(), pageable.getPageSize());
		if (SessionComponent.isAdmin()) {
			iotxDataContents = iotxDataContentService.findByContentAndAlarm(content, isAlarm, contentPage);
		} else {
			iotxDataContents = iotxDataContentService.findByContentAndAlarmAndCompanyName(content, isAlarm,
					account.getCompany().getName(), contentPage);
		}
		List<BigInteger> ids = iotxDataContents.getContent().stream().map(c -> new BigInteger(c.getId()))
				.collect(Collectors.toList());
		return findAll(QIotxData.iotxData.id.in(ids), contentPage);
	}

	@Override
	public IotxData findOne(BigInteger id) {
		return iotxDataDao.findOne(id);
	}

	@Override
	public Page<IotxData> findBySensorSN(String sensorSN, Pageable pageable) {
		return iotxDataDao.findBySensorSN(sensorSN, pageable);
	}

	@Override
	@Transactional
	public void parse(InputStream inputStream) throws Exception {
		List<String> lines = IOUtils.readLines(inputStream, Charset.forName("utf-8"));
		List<IotxData> iotxDatas = lines.parallelStream().map(line -> {
			String[] vals = line.split("\t");
			if (vals.length == 3) {
				return vals;
			} else {
				return null;
			}
		}).filter(vals -> vals != null).map(vals -> {
			Double date = Double.parseDouble(vals[0]) * 1000;
			return new IotxData(vals[1], Double.parseDouble(vals[2]), new Date(((Number) date).longValue()));
		}).distinct().collect(Collectors.toList());

		try {
			// 跳过重复错误
			BulkOperations bulkOps = mongoTemplate.bulkOps(BulkMode.UNORDERED, IotxData.class);
			bulkOps.insert(iotxDatas);
			bulkOps.execute();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
