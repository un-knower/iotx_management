package com.anosi.asset.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONArray;
import com.anosi.asset.model.jpa.Company;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.Iotx.Status;
import com.querydsl.core.types.Predicate;

public interface IotxService extends BaseJPAService<Iotx> {

	public Iotx setIotxDistrict(Iotx iotx);

	JSONArray ascertainArea(Predicate predicate);

	/***
	 * 根据模糊搜索的content,获取到iotxContent,进而获取到iotx
	 * 
	 * @param content
	 * @param pageable
	 * @return
	 */
	public Page<Iotx> findByContentSearch(String content, Pageable pageable);

	public Iotx findBySerialNo(String serialNo);

	/***
	 * 远程配置
	 * 
	 * @param iotx
	 * @param company
	 */
	public void remoteUpdate(Iotx iotx, Company company);
	
	/***
	 * 根据公司和在线离线状态进行count
	 * 
	 * @param companyId
	 * @param status
	 * @return
	 */
	public Long countByCompanyAndStatus(Long companyId, Status status);
}
