package com.anosi.asset.service;

import com.anosi.asset.model.jpa.SensorCategory;

public interface SensorCategoryService {

	public Iterable<SensorCategory> findAll();
	
}
