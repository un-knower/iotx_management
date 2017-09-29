package com.anosi.asset.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONArray;
import com.anosi.asset.model.jpa.Company;
import com.anosi.asset.model.jpa.Iotx;
import com.querydsl.core.types.Predicate;

public interface IotxService extends BaseService<Iotx, Long> {

	public Iotx setIotxDistrict(Iotx iotx);

	JSONArray ascertainArea(Predicate predicate);

	/***
	 * 根据模糊搜索的content,获取到iotxContent,进而获取到iotx
	 * 
	 * @param content
	 * @param predicate
	 * @param pageable
	 * @return
	 */
	public Page<Iotx> findByContentSearch(String content, Predicate predicate,Pageable pageable);

	public Iotx findBySerialNo(String serialNo);

	/***
	 * 远程配置
	 * 
	 * @param iotx
	 * @param company
	 */
	public void remoteUpdate(Iotx iotx, Company company);
}
