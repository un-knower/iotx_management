package com.anosi.asset.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.anosi.asset.model.jpa.Dust;
import com.querydsl.core.types.Predicate;

public interface DustService extends BaseService<Dust, Long> {

	public Page<Dust> findByContentSearch(String content, Predicate predicate, Pageable pageable);

}
