package com.anosi.asset.service;

import com.anosi.asset.model.jpa.IotxStatusPer;

public interface IotxStatusPerService extends BaseJPAService<IotxStatusPer>{

	/***
	 * 将前一天的iotx在线离线数量存入数据库
	 */
	void saveIotxStatusPerTimer();

}
