package com.anosi.asset.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.anosi.asset.model.jpa.Sensor;
import com.querydsl.core.types.Predicate;

public interface SensorService {
	
	public Page<Sensor> findAll(Predicate predicate, Pageable pageable);

	public Sensor findById(Long sensorId);
	
	public Sensor saveSensor(Sensor sensor);
	
	public void deleteSensor(Sensor sensor);

	public boolean exists(Predicate predicate);

	public Iterable<Sensor> findAll(Predicate predicate);
	
	public Sensor findBySerialNo(String serialNo);
	
}
