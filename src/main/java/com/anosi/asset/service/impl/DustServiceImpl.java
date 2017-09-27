package com.anosi.asset.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.anosi.asset.component.SessionUtil;
import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.DustDao;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.elasticsearch.DustContent;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.Dust;
import com.anosi.asset.model.jpa.QDust;
import com.anosi.asset.service.DustContentService;
import com.anosi.asset.service.DustService;
import com.querydsl.core.types.Predicate;

@Service("dustService")
@Transactional
public class DustServiceImpl extends BaseServiceImpl<Dust> implements DustService {
	
	private static final Logger logger = LoggerFactory.getLogger(DustServiceImpl.class);

	@Autowired
	private DustDao dustDao;
	@Autowired
	private DustContentService dustContentService;

	@Override
	public BaseJPADao<Dust> getRepository() {
		return dustDao;
	}
	
	/***
	 * 重写save,保存dust的同时，将@Content标记的字段内容提取，存储到dustContent中
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Dust save(Dust dust) {
		dustDao.save(dust);
		try {
			dustContentService.save(dust);
		} catch (Exception e) {
			throw new CustomRunTimeException(e.getMessage());
		}
		return dust;
	}


	@Override
	public Page<Dust> findByContentSearch(String content, Predicate predicate, Pageable pageable) {
		Account account = SessionUtil.getCurrentUser();
		Page<DustContent> dustContents;
		// 防止sort报错，只获取pageable的页数和size
		logger.debug("page:{},size:{}",pageable.getPageNumber(), pageable.getPageSize());
		Pageable contentPage = new PageRequest(pageable.getPageNumber(), pageable.getPageSize());
		if (account.isAdmin()) {
			dustContents = dustContentService.findByContent(content, contentPage);
		} else {
			dustContents = dustContentService.findByContent(account.getCompany().getName(), content, contentPage);
		}
		List<Long> ids = dustContents.getContent().stream().map(c -> Long.parseLong(c.getId()))
				.collect(Collectors.toList());
		return findAll(QDust.dust.id.in(ids).and(predicate), pageable);
	}

}
