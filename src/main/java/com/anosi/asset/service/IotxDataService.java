package com.anosi.asset.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.anosi.asset.model.mongo.IotxData;
import com.querydsl.core.types.Predicate;

public interface IotxDataService extends BaseMongoService<IotxData>{

	/***
	 * 根据模糊搜索的content,获取到iotxDataContent,进而获取到iotxData
	 * 
	 * @param content
	 * @param pageable
	 * @return
	 */
	public Page<IotxData> findByContentSearch(String content, Pageable pageable);

	/****
	 * 重载
	 * 
	 * @param content
	 * @param isAlarm
	 * @param pageable
	 * @return
	 */
	public Page<IotxData> findByContentSearch(String content, Boolean isAlarm, Pageable pageable);

	/***
	 * 获取动态线图上的数据
	 * 
	 * @param predicate
	 * @param frequency
	 * @param timeUnit
	 *            时间单位，用来区分月线，周线，日线等，以此来获取需要取出的数据数量
	 *            example:如果要看周线，就将一周时间换算成秒数:7*24*60*60,然后除以取样频率就获得了要获取的row总数
	 * @param sort
	 * @return
	 * @throws Exception
	 */
	Page<IotxData> findDynamicData(Predicate predicate, Double frequency, Integer timeUnit, Sort sort) throws Exception;

	Long countBysensorSN(String sensorSN);

	Long countByiotxSN(String iotxSN);

}
