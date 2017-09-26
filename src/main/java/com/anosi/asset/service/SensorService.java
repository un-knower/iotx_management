package com.anosi.asset.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.anosi.asset.model.jpa.Sensor;
import com.querydsl.core.types.Predicate;

public interface SensorService extends BaseService<Sensor, Long> {

	public Sensor findBySerialNo(String serialNo);

	public Page<Sensor> findSensorByContentSearch(String content, Predicate predicate, Pageable pageable);

}
