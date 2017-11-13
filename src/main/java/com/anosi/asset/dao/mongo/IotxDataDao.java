package com.anosi.asset.dao.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.anosi.asset.model.mongo.IotxData;

public interface IotxDataDao extends BaseMongoDao<IotxData> {

	public Page<IotxData> findBySensorSN(String sensorSN, Pageable pageable);
}
