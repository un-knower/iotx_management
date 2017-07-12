package com.anosi.asset.dao.jpa;

import org.springframework.data.jpa.repository.EntityGraph;

import com.anosi.asset.model.jpa.Sensor;

public interface SensorDao extends BaseJPADao<Sensor>{

	@EntityGraph(attributePaths = {"sensorCategory","sensor.iotx.company"})
	public Sensor findBySerialNoEquals(String serialNo);
	
}
