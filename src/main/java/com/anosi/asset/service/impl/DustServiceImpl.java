package com.anosi.asset.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.anosi.asset.component.SessionUtil;
import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.DustDao;
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

	@Autowired
	private DustDao dustDao;
	@Autowired
	private DustContentService dustContentService;

	@Override
	public BaseJPADao<Dust> getRepository() {
		return dustDao;
	}

	@Override
	public Page<Dust> findDustByContentSearch(String content, Predicate predicate, Pageable pageable) {
		Account account = SessionUtil.getCurrentUser();
		Page<DustContent> dustContents;
		if (account.isAdmin()) {
			dustContents = dustContentService.findByContent(content, pageable);
		} else {
			dustContents = dustContentService.findByContent(account.getCompany().getName(), content, pageable);
		}
		List<Long> ids = dustContents.getContent().stream().map(c -> Long.parseLong(c.getId()))
				.collect(Collectors.toList());
		return findAll(QDust.dust.id.in(ids).and(predicate), pageable);
	}

}
