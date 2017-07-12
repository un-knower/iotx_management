package com.anosi.asset.dao.mongo;

import java.math.BigInteger;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.anosi.asset.model.mongo.IotxData;

public interface IotxDataDao extends MongoRepository<IotxData, BigInteger>,QueryDslPredicateExecutor<IotxData>{
	
	/***
	 * 统计传感器的告警数量
	 * @param sensorSN
	 * @return
	 */
	public Long countBysensorSNEquals(String sensorSN);
	
	/***
	 * 统计iotx节点的告警数量
	 * @param iotxSN
	 * @return
	 */
	public Long countByiotxSNEquals(String iotxSN);
	
}
