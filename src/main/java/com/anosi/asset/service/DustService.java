package com.anosi.asset.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.anosi.asset.model.jpa.Dust;

public interface DustService extends BaseJPAService<Dust> {

	public Page<Dust> findByContentSearch(String content,Pageable pageable);

	public Dust findBySerialNo(String serialNo);

	/***
	 * 远程配置dust
	 * 
	 * @param dust
	 * @param name
	 * @param frequency
	 * @param isWorked
	 */
	public void remoteUpdate(Dust dust, String name, Double frequency, boolean isWorked);

}
