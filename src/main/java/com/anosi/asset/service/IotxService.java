package com.anosi.asset.service;

import com.alibaba.fastjson.JSONArray;
import com.anosi.asset.model.jpa.Iotx;
import com.querydsl.core.types.Predicate;

public interface IotxService extends BaseService<Iotx, Long>{

	public Iotx setIotxDistrict(Iotx iotx);

	JSONArray ascertainArea(Predicate predicate);
	
}
