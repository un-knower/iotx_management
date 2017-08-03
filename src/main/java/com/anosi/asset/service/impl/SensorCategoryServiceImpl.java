package com.anosi.asset.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.SensorCategoryDao;
import com.anosi.asset.model.jpa.SensorCategory;
import com.anosi.asset.service.SensorCategoryService;

@Service("sensorCategoryService")
@Transactional
public class SensorCategoryServiceImpl extends BaseServiceImpl<SensorCategory> implements SensorCategoryService{

	@Autowired
	private SensorCategoryDao sensorCategoryDao;

	@Override
	public BaseJPADao<SensorCategory> getRepository() {
		return sensorCategoryDao;
	}

}
