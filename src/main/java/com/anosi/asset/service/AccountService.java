package com.anosi.asset.service;

import com.alibaba.fastjson.JSONArray;
import com.anosi.asset.model.jpa.Account;

public interface AccountService extends BaseJPAService<Account> {
	
	public Account findByLoginId(String loginId);

	/****
	 * 将权限转化成zTree
	 * @param id
	 * @return
	 */
	public JSONArray parseRoleFunctionToTree(Long id);

	/***
	 * 
	 * @param account
	 * @param password
	 * @param roles
	 * @param roleFunctionGroups
	 * @param selRolesFunctionNode
	 * @return
	 * @throws Exception 
	 */
	public Account save(Account account, String password, String[] roles,String[] roleFunctionGroups, String[] selRolesFunctionNode) throws Exception;

	/***
	 * 为account设置权限
	 * @param account
	 * @param selRolesFunctionNode
	 */
	public void resolveRoleFunction(Account account, String[] selRolesFunctionNode);
	
}
