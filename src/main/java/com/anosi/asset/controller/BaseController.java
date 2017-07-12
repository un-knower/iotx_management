package com.anosi.asset.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.jpa.Account;

public class BaseController<T> extends GlobalController<T>{

	/***
	 * 注册date
	 * @param request
	 * @param binder
	 */
	@InitBinder
	protected void initDate(HttpServletRequest request, ServletRequestDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
	
	/***
	 * 注册double
	 * @param request
	 * @param binder
	 */
	@InitBinder
	protected void initDouble(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Double.class,new CustomNumberEditor(Double.class,true));
	}
	
	/***
	 * 在每个controller调用前，将menuId加入session
	 * @param id
	 * @param model
	 */
	@ModelAttribute
	public void setMenuIdIntoSession(@RequestParam(value = "menuId", required = false) String menuId){
		if(StringUtils.isNoneBlank(menuId)){
			Subject currentUser = SecurityUtils.getSubject();  
			Session session = currentUser.getSession();
			session.setAttribute("menuId", menuId);
		}
	}
	
	/***
	 * 判断request中的companyCode和session中用户的companyCode是否相等
	 * @param companyId
	 */
	protected void checkCompany(String companyCode){
		Subject currentUser = SecurityUtils.getSubject();
		if(!currentUser.hasRole("admin")){
			if(StringUtils.isBlank(companyCode)){
				throw new CustomRunTimeException("the companyCode in request can not be null");
			}else{
				Session session = currentUser.getSession();
				Account account = (Account) session.getAttribute("user");
				if(!Objects.equals(companyCode, account.getCompany().getCode())){
					throw new CustomRunTimeException("the companyCode in request is not equals the one which in session");
				}
			}
		}
	}
	
}
