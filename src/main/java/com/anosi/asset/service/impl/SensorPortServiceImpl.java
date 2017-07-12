package com.anosi.asset.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.dao.jpa.SensorPortDao;
import com.anosi.asset.model.jpa.SensorPort;
import com.anosi.asset.service.SensorPortService;

@Service("sensorPortService")
@Transactional
public class SensorPortServiceImpl implements SensorPortService{
	
	@Autowired
	private SensorPortDao sensorPortDao;

	@Override
	public Iterable<SensorPort> findAll() {
		return sensorPortDao.findAll();
	}

}
