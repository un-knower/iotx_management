package com.anosi.asset.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.dao.elasticsearch.BaseContentDao;
import com.anosi.asset.dao.elasticsearch.DustContentDao;
import com.anosi.asset.model.elasticsearch.DustContent;
import com.anosi.asset.model.jpa.Dust;
import com.anosi.asset.service.DustContentService;

@Service("dustContentService")
@Transactional
public class DustContentServiceImpl extends BaseContentServiceImpl<DustContent, String, Dust>
		implements DustContentService {

	@Autowired
	private DustContentDao dustContentDao;

	@Override
	public BaseContentDao<DustContent, String> getRepository() {
		return dustContentDao;
	}

	@Override
	public DustContent save(Dust dust) throws Exception {
		String id = String.valueOf(dust.getId());
		DustContent dustContent = dustContentDao.findOne(id);
		if (dustContent == null) {
			dustContent = new DustContent();
			dustContent.setId(id);
		}
		dustContent.setCompanyName(dust.getIotx().getCompany().getName());
		dustContent = setCommonContent(dustContent, dust);
		return dustContentDao.save(dustContent);
	}

	@Override
	public <S extends Dust> Iterable<DustContent> save(Iterable<S> obs) throws Exception {
		List<DustContent> dustContents = new ArrayList<>();
		for (Dust dust : obs) {
			String id = String.valueOf(dust.getId());
			DustContent dustContent = dustContentDao.findOne(id);
			if (dustContent == null) {
				dustContent = new DustContent();
				dustContent.setId(id);
			}
			dustContent.setCompanyName(dust.getIotx().getCompany().getName());
			dustContent = setCommonContent(dustContent, dust);
			dustContents.add(dustContent);
		}
		return dustContentDao.save(dustContents);
	}

}
