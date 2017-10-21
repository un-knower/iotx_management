package com.anosi.asset.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.anosi.asset.model.jpa.Dust;
import com.querydsl.core.types.Predicate;

public interface DustService extends BaseJPAService<Dust> {

	public Page<Dust> findByContentSearch(String content, Predicate predicate, Pageable pageable);

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
