package com.anosi.asset.service;

import com.anosi.asset.model.jpa.SensorInterface;

public interface SensorInterfaceService {

	public Iterable<SensorInterface> findAll();
	
}
