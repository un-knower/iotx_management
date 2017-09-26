package com.anosi.asset.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.DustDao;
import com.anosi.asset.model.jpa.Dust;
import com.anosi.asset.service.DustService;

@Service("dustService")
@Transactional
public class DustServiceImpl extends BaseServiceImpl<Dust> implements DustService{

	@Autowired
	private DustDao dustDao;
	
	@Override
	public BaseJPADao<Dust> getRepository() {
		return dustDao;
	}

}
