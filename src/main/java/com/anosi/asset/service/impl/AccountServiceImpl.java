package com.anosi.asset.service.impl;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anosi.asset.dao.jpa.AccountDao;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.service.AccountService;

@Service("accountService")
@Transactional
public class AccountServiceImpl implements AccountService{
	
	private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
	
	@Autowired
	private AccountDao accountDao;
	
	@Override
	public Account findByLoginId(String loginId) {
		logger.debug("findByLoginId:{}",loginId);
		return this.accountDao.findByLoginId(loginId);
	}

	@Override
	public Account save(Account account) {
		return this.accountDao.save(account);
	}

}
