package com.anosi.asset.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.anosi.asset.model.mongo.IotxData;
import com.querydsl.core.types.Predicate;

public interface IotxDataService{

	/***
	 * 获取动态线图上的数据
	 * @return
	 * @throws Exception
	 */
	public List<IotxData> findDynamicData(Predicate predicate,Integer timeUnit,Sort sort) throws Exception;

	public Page<IotxData> findAll(Predicate predicate, Pageable pageable);
	
	public Long countBysensorSN(String sensorSN);
	
	public Long countByiotxSN(String iotxSN);
	
	public IotxData save(IotxData iotxData);

	public Iterable<IotxData> save(Iterable<IotxData> iotxDatas);
	
}
