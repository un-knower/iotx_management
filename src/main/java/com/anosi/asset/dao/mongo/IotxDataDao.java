package com.anosi.asset.dao.mongo;

import com.anosi.asset.model.mongo.IotxData;

public interface IotxDataDao extends BaseMongoDao<IotxData>{
	
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
