package com.anosi.asset.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.anosi.asset.model.mongo.IotxData;
import com.querydsl.core.types.Predicate;

public interface IotxDataService {

	/***
	 * 获取动态线图上的数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<IotxData> findDynamicData(Predicate predicate, Integer timeUnit, Sort sort) throws Exception;

	public Page<IotxData> findAll(Predicate predicate, Pageable pageable);

	public Long countBysensorSN(String sensorSN);

	public Long countByiotxSN(String iotxSN);

	public IotxData save(IotxData iotxData);

	/***
	 * 批量添加
	 * 
	 * @param iotxDatas
	 * @return
	 */
	public <S extends IotxData> Iterable<S> save(Iterable<S> iotxDatas);

	/***
	 * 根据模糊搜索的content,获取到iotxDataContent,进而获取到iotxData
	 * 
	 * @param content
	 * @param predicate
	 * @param pageable
	 * @return
	 */
	public Page<IotxData> findByContentSearch(String content, Predicate predicate, Pageable pageable);

	/****
	 * 重载
	 * 
	 * @param content
	 * @param isAlarm
	 * @param predicate
	 * @param pageable
	 * @return
	 */
	public Page<IotxData> findByContentSearch(String content, Boolean isAlarm, Predicate predicate, Pageable pageable);

}
