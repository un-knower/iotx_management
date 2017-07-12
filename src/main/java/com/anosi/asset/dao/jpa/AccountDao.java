package com.anosi.asset.dao.jpa;

import org.springframework.data.jpa.repository.EntityGraph;

import com.anosi.asset.model.jpa.Account;

public interface AccountDao extends BaseJPADao<Account>{

	@EntityGraph(attributePaths = {"company"})
	public Account findByLoginId(String loginId);
	
}
