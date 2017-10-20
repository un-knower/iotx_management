package com.anosi.asset.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.component.I18nComponent;
import com.anosi.asset.component.SessionComponent;
import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.model.jpa.BaseEntity;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

@Transactional
public abstract class BaseServiceImpl<T extends BaseEntity> {
	
	@Autowired
	protected SessionComponent sessionComponent;
	@Autowired
	protected I18nComponent i18nComponent;

	public abstract BaseJPADao<T> getRepository();

	public <S extends T> S save(S entity) {
		return getRepository().save(entity);
	}

	public <S extends T> Iterable<S> save(Iterable<S> entities) {
		return getRepository().save(entities);
	}

	public T findOne(Long id) {
		return getRepository().findOne(id);
	}

	public boolean exists(Long id) {
		return getRepository().exists(id);
	}

	public List<T> findAll() {
		return getRepository().findAll();
	}

	public List<T> findAll(Iterable<Long> ids) {
		return getRepository().findAll(ids);
	}

	public long count() {
		return getRepository().count();
	}

	public void delete(Long id) {
		getRepository().delete(id);
	}

	public void delete(T entity) {
		getRepository().delete(entity);
	}

	public void delete(Iterable<? extends T> entities) {
		getRepository().delete(entities);
	}

	public void deleteAll() {
		getRepository().deleteAll();
	}

	public Iterable<T> findAll(Sort sort) {
		return getRepository().findAll(sort);
	}

	public Page<T> findAll(Pageable pageable) {
		return getRepository().findAll(pageable);
	}

	public void flush() {
		getRepository().flush();
	}

	public <S extends T> S saveAndFlush(S entity) {
		return getRepository().saveAndFlush(entity);
	}

	public void deleteInBatch(Iterable<T> entities) {
		getRepository().deleteInBatch(entities);
	}

	public void deleteAllInBatch() {
		getRepository().deleteAllInBatch();
	}

	public T getOne(Long id) {
		return getRepository().getOne(id);
	}

	public T findOne(Predicate predicate) {
		return getRepository().findOne(predicate);
	}

	public Iterable<T> findAll(Predicate predicate) {
		return getRepository().findAll(predicate);
	}

	public Iterable<T> findAll(Predicate predicate, Sort sort) {
		return getRepository().findAll(predicate, sort);
	}

	public Iterable<T> findAll(Predicate predicate, OrderSpecifier<?>... orders) {
		return getRepository().findAll(predicate, orders);
	}

	public Iterable<T> findAll(OrderSpecifier<?>... orders) {
		return getRepository().findAll(orders);
	}

	public Page<T> findAll(Predicate predicate, Pageable pageable) {
		return getRepository().findAll(predicate, pageable);
	}

	public long count(Predicate predicate) {
		return getRepository().count(predicate);
	}

	public boolean exists(Predicate predicate) {
		return getRepository().exists(predicate);
	}

}
