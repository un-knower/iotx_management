package com.anosi.asset.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.SensorPortDao;
import com.anosi.asset.model.jpa.SensorPort;
import com.anosi.asset.service.SensorPortService;

@Service("sensorPortService")
@Transactional
public class SensorPortServiceImpl extends BaseServiceImpl<SensorPort> implements SensorPortService{
	
	@Autowired
	private SensorPortDao sensorPortDao;

	@Override
	public BaseJPADao<SensorPort> getRepository() {
		return sensorPortDao;
	}

}
