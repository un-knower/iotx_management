package com.anosi.asset.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONArray;
import com.anosi.asset.model.jpa.Iotx;
import com.querydsl.core.types.Predicate;

public interface IotxService {

	public Page<Iotx> findAll(Predicate predicate, Pageable pageable);
	
	public Iterable<Iotx> findAll(Predicate predicate);
	
	public Iotx saveIotx(Iotx iotx);

	public void deleteIotx(Iotx iox);
	
	public Iotx findById(Long id);
	
	public boolean exists(Predicate predicate);
	
	public Iotx setIoxDistrict(Iotx iotx);

	JSONArray ascertainArea(Predicate predicate);
	
}
