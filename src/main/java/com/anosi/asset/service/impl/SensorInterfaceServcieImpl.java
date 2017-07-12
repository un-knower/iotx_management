package com.anosi.asset.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.dao.jpa.SensorInterfaceDao;
import com.anosi.asset.model.jpa.SensorInterface;
import com.anosi.asset.service.SensorInterfaceService;

@Service("sensorInterfaceService")
@Transactional
public class SensorInterfaceServcieImpl implements SensorInterfaceService{

	@Autowired
	private SensorInterfaceDao sensorInterfaceDao;
	
	@Override
	public Iterable<SensorInterface> findAll() {
		return sensorInterfaceDao.findAll();
	}

}
