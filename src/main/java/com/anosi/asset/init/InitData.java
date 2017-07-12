package com.anosi.asset.init;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.City;
import com.anosi.asset.model.jpa.Company;
import com.anosi.asset.model.jpa.District;
import com.anosi.asset.model.jpa.Privilege;
import com.anosi.asset.model.jpa.Province;
import com.anosi.asset.model.jpa.Role;
import com.anosi.asset.model.jpa.RoleFunction;
import com.anosi.asset.model.jpa.RoleFunctionBtn;
import com.anosi.asset.service.AccountService;
import com.anosi.asset.service.CityService;
import com.anosi.asset.service.CompanyService;
import com.anosi.asset.service.DistrictService;
import com.anosi.asset.service.PrivilegeService;
import com.anosi.asset.service.ProvinceService;
import com.anosi.asset.service.RoleFunctionBtnService;
import com.anosi.asset.service.RoleFunctionService;
import com.anosi.asset.service.RoleService;
import com.anosi.asset.util.PasswordEncry;

/***
 * 进行一些数据的初始化工作
 * @author jinyao
 *
 */
@Component
public class InitData {
	
	private static final Logger logger = LoggerFactory.getLogger(InitData.class);
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private RoleFunctionService roleFunctionService;
	@Autowired
	private RoleFunctionBtnService roleFunctionBtnService;
	@Autowired
	private PrivilegeService privilegeService;
	@Autowired
	private TransactionTemplate transactionTemplate;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ProvinceService provinceService;
	@Autowired
	private CityService cityService;
	@Autowired
	private DistrictService districtService;
	
	@PostConstruct
	public void init(){
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				initAdmin();
				initCompany();
				initRole();
				initRoleFunction();
				initRoleFunctionBtn();
				initProvinceData();
				initCityData();
				initDistrictData();
			}
		});
	}

	/***
	 * 初始化admin
	 */
	private void initAdmin(){
		Account account = this.accountService.findByLoginId("admin");
		
		if(account==null){
			account = new Account();
			account.setName("admin");
			account.setLoginId("admin");
			account.setPassword("123456");
			try {
				//设置密码
				PasswordEncry.encrypt(account);
			} catch (Exception e) {
				e.printStackTrace();
			}
			accountService.save(account);
		}
	}
	
	private void initCompany(){
		Locale locale = LocaleContextHolder.getLocale();
		String companyName = messageSource.getMessage("anosi", null,locale);
		if(companyService.findByName(companyName)==null){
			Company company = new Company();
			company.setName(companyName);
			company.setCode("anosi");
			companyService.save(company);
			Account account = this.accountService.findByLoginId("admin");
			account.setCompany(company);
		}
	}
	
	/***
	 * 初始化role
	 */
	private void initRole(){
		SAXReader reader = new SAXReader();
		try {
			String path = this.getClass().getClassLoader().getResource("initResources/initRole.xml").getPath();
			Document document = reader.read(new File(path));
			Element root = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> list = root.elements();
			//如果xml中的数量比role表中的数量多，则表示有新增的
			if(list.size()>roleService.count()){
				logger.debug("init extemal role");
				//查出admin,为了后面给admin添加role
				Account account = this.accountService.findByLoginId("admin");
				
				for (Element element : list) {
					String roleCode = element.attributeValue("roleCode");
					if(roleService.findByRoleCode(roleCode)==null){
						Role role=new Role();
						role.setRoleCode(roleCode);
						roleService.save(role);
						account.getRoleList().add(role);
					}
				}
			}else{
				logger.debug("unnecessary init role ");
			}
		} catch (DocumentException e) {
			logger.debug("init role failed");
			e.printStackTrace();
		}  
	}
	
	/***
	 * 初始化menu权限
	 */
	private void initRoleFunction(){
		SAXReader reader = new SAXReader();
		try {
			String path = this.getClass().getClassLoader().getResource("initResources/initRoleFunction.xml").getPath();
			Document document = reader.read(new File(path));
			Element root = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> list = root.elements();
			//如果xml中的数量比roleFunction表中的数量多，则表示有新增的
			if(list.size()>roleFunctionService.count()){
				logger.debug("init extemal roleFunction");
				//查出admin,为了后面给admin添加roleFunction
				Account account = this.accountService.findByLoginId("admin");

				for (Element element : list) {
					String roleFunctionPageId = element.attributeValue("roleFunctionPageId");
					if(roleFunctionService.findByRoleFunctionPageId(roleFunctionPageId)==null){
						RoleFunction roleFunction=new RoleFunction();
						roleFunction.setRoleFunctionPageId(roleFunctionPageId);
						
						/*-----------start:检查是否有父级的roleFunction-------------*/
						String parentRoleFunctionPageId = element.attributeValue("parentRoleFunctionPageId");
						if(StringUtils.isNoneBlank(parentRoleFunctionPageId)){
							RoleFunction parentRoleFunction = roleFunctionService.findByRoleFunctionPageId(parentRoleFunctionPageId);
							if(parentRoleFunction==null){
								//如果不存在这个父级rolefunction，则抛异常
								throw new CustomRunTimeException("there is no parentRoleFunctionPageId with pageId:"+parentRoleFunctionPageId);
							}else{
								roleFunction.setParentRoleFunction(parentRoleFunction);
							}
						}
						/*-----------end:检查是否有父级的roleFunction-------------*/
						
						roleFunctionService.save(roleFunction);
						
						Privilege privilege = new Privilege();
						privilege.setRoleFunction(roleFunction);
						privilege.setAccount(account);
						privilegeService.save(privilege);
					}
				}
			}else{
				logger.debug("unnecessary init roleFunction");
			}
		} catch (DocumentException e) {
			logger.debug("init roleFunction failed");
			e.printStackTrace();
		}  
	}
	
	/****
	 * 初始化按钮权限
	 */
	private void initRoleFunctionBtn(){
		SAXReader reader = new SAXReader();
		try {
			String path = this.getClass().getClassLoader().getResource("initResources/initRoleFunctionBtn.xml").getPath();
			Document document = reader.read(new File(path));
			Element root = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> list = root.elements();
			//如果xml中的数量比roleFunctionBtn表中的数量多，则表示有新增的
			if(list.size()>roleFunctionBtnService.count()){
				logger.debug("init extemal roleFunction");
				//查出admin,为了后面给admin添加roleFunctionBtn
				Account account = this.accountService.findByLoginId("admin");
				
				for (Element element : list) {
					String roleFunctionPageId = element.attributeValue("roleFunctionPageId");
					String btnId = element.attributeValue("btnId");
					if(roleFunctionBtnService.findByBtnIdAndRoleFunction(btnId, roleFunctionPageId)==null){
						
						RoleFunction roleFunction = this.roleFunctionService.findByRoleFunctionPageId(roleFunctionPageId);
						if(this.roleFunctionService.findByRoleFunctionPageId(roleFunctionPageId)==null){
							throw new CustomRunTimeException("there is no parentRoleFunctionPageId with pageId:"+roleFunctionPageId);
						}else{
							RoleFunctionBtn roleFunctionBtn = new RoleFunctionBtn();
							roleFunctionBtn.setBtnId(btnId);
							roleFunctionBtn.setRoleFunction(roleFunction);
							roleFunctionBtnService.save(roleFunctionBtn);
							
							/*-------start:为admin添加这个按钮权限-------*/
							Privilege privilege = this.privilegeService.findByAccountAndRoleFunction(account.getLoginId(), roleFunctionPageId);
							if(privilege!=null){
								privilege.getRoleFunctionBtnList().add(roleFunctionBtn);
							}
							/*-------end:为admin添加这个按钮权限-------*/
						}
					}
				}
			}else{
				logger.debug("unnecessary init roleFunctionBtn");
			}
		} catch (DocumentException e) {
			logger.debug("init roleFunctionBtn failed");
			e.printStackTrace();
		}
	}
	
	private void initProvinceData() {
		logger.debug("InitDataImpl.initProvinceData");
		/**
		 * 初始化省份数据
		 */
		if (this.provinceService.count() == 0) {
			List<Province> provinceList = new ArrayList<>();
			try {
				SAXReader reader = new SAXReader();
				String path = this.getClass().getClassLoader().getResource("initResources/Provinces.xml").getPath()
						.replace("%20", " ");
				Document document = reader.read(new File(path));
				Element root = document.getRootElement();
				@SuppressWarnings("unchecked")
				List<Element> list = root.elements();
				for (Iterator<Element> i = list.iterator(); i.hasNext();) {
					Element resourceitem = (Element) i.next();
					Province p = new Province();
					p.setPid(resourceitem.attributeValue("ID"));
					p.setName(resourceitem.attributeValue("ProvinceName"));
					provinceList.add(p);
				}
				this.provinceService.save(provinceList);
				logger.debug("省份数据初始化成功！");
			} catch (DocumentException e) {
				logger.debug("省份数据初始化失败！");
			}
		}
	}

	private void initCityData() {
		logger.debug("InitDataImpl.initCityData");
		if (this.cityService.count() == 0) {
			/**
			 * 初始化城市数据
			 */
			List<City> cityList = new ArrayList<>();
			try {
				SAXReader reader = new SAXReader();
				String path = this.getClass().getClassLoader().getResource("initResources/Cities.xml").getPath().replace("%20",
						" ");
				Document document = reader.read(new File(path));
				Element root = document.getRootElement();
				@SuppressWarnings("unchecked")
				List<Element> list = root.elements();
				for (Iterator<Element> i = list.iterator(); i.hasNext();) {
					Element resourceitem = (Element) i.next();
					City c = new City();
					c.setCid(resourceitem.attributeValue("ID"));
					c.setName(resourceitem.attributeValue("CityName"));

					c.setProvince(this.provinceService.findByPID(resourceitem.attributeValue("PID")));
					cityList.add(c);
				}
				this.cityService.save(cityList);
				logger.debug("城市数据初始化成功！");
			} catch (DocumentException e) {
				logger.debug("城市数据初始化失败！");
			}
		}
	}

	private void initDistrictData() {
		logger.debug("InitDataImpl.initDistrictData");
		if (this.districtService.count() == 0) {
			/**
			 * 初始化地区数据
			 */
			List<District> districtList = new ArrayList<>();
			try {
				SAXReader reader = new SAXReader();
				String path = this.getClass().getClassLoader().getResource("initResources/Districts.xml").getPath()
						.replace("%20", " ");
				Document document = reader.read(new File(path));
				Element root = document.getRootElement();
				@SuppressWarnings("unchecked")
				List<Element> list = root.elements();
				for (Iterator<Element> i = list.iterator(); i.hasNext();) {
					Element resourceitem = (Element) i.next();
					District d = new District();
					d.setId(Long.parseLong(resourceitem.attributeValue("ID")));
					d.setName(resourceitem.attributeValue("DistrictName"));
					d.setCity(
							this.cityService.findByCID(resourceitem.attributeValue("CID")));
					districtList.add(d);
				}
				this.districtService.save(districtList);
				logger.debug("区县数据初始化成功！");
			} catch (DocumentException e) {
				logger.debug("区县数据初始化失败！");
			}
		}
	}
	
}
