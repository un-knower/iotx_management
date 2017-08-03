package com.anosi.asset.service;

import com.anosi.asset.model.jpa.Sensor;

public interface SensorService extends BaseService<Sensor, Long>{
	
	public Sensor findBySerialNo(String serialNo);
	
}
