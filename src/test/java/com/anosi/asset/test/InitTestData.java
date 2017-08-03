package com.anosi.asset.test;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.IotxManagementApplication;
import com.anosi.asset.component.PasswordEncry;
import com.anosi.asset.dao.jpa.AccountDao;
import com.anosi.asset.dao.jpa.CompanyDao;
import com.anosi.asset.dao.jpa.PrivilegeDao;
import com.anosi.asset.dao.jpa.RoleDao;
import com.anosi.asset.dao.jpa.RoleFunctionDao;
import com.anosi.asset.dao.jpa.SensorCategoryDao;
import com.anosi.asset.dao.jpa.SensorInterfaceDao;
import com.anosi.asset.dao.jpa.SensorPortDao;
import com.anosi.asset.dao.mongo.IotxDataDao;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.Company;
import com.anosi.asset.model.jpa.Privilege;
import com.anosi.asset.model.jpa.Role;
import com.anosi.asset.model.jpa.RoleFunction;
import com.anosi.asset.model.jpa.RoleFunctionBtn;
import com.anosi.asset.model.jpa.SensorCategory;
import com.anosi.asset.model.jpa.SensorInterface;
import com.anosi.asset.model.jpa.SensorPort;
import com.anosi.asset.model.mongo.IotxData;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IotxManagementApplication.class)
@Transactional
public class InitTestData {
	
	private static final Logger logger = LoggerFactory.getLogger(InitTestData.class);
	
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private SensorInterfaceDao sensorInterfaceDao;
	@Autowired
	private SensorPortDao sensorPortDao;
	@Autowired
	private SensorCategoryDao sensorCategoryDao;
	@Autowired
	private IotxDataDao iotxDataDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private RoleFunctionDao roleFunctionDao;
	@Autowired
	private PrivilegeDao privilegeDao;
	
	@Test
	@Rollback(false)
	public void saveIotxData(){
		IotxData iotxData = new IotxData();
		iotxData.setSensorSN("abc123");
		iotxDataDao.save(iotxData);
	}
	
	@Test
	@Rollback(false)
	public void initAccounAndRole(){
		Account account = accountDao.findByLoginId("admin");
		for(int i=1;i<5;i++){
			Role role=new Role();
			role.setRoleCode("testcode"+i);
			roleDao.save(role);
			account.getRoleList().add(role);
		}
	}
	
	@Test
	public void testMerge(){
		Account account = new Account();
		account.setId((long) 1);
		account.setName("hello");
		accountDao.save(account);
		logger.debug(account.getPassword());
		logger.debug(account.getName());
	}
	
	@Test
	@Rollback(false)
	public void testFlush(){
		Account account = accountDao.findByLoginId("admin");
		account.setName("123");
	}
	
	@Test
	@Rollback(false)
	public void initSensorRealted(){
		SensorInterface sensorInterface = new SensorInterface();
		sensorInterface.setName("测试接口");
		sensorInterface=sensorInterfaceDao.save(sensorInterface);
		
		SensorPort sensorPort = new SensorPort();
		sensorPort.setName("测试端口");
		sensorPort.setSensorInterface(sensorInterface);
		sensorPortDao.save(sensorPort);
		
		SensorCategory sensorCategory = new SensorCategory();
		sensorCategory.setName("温度传感器");
		sensorCategoryDao.save(sensorCategory);
	}
	
	@Test
	@Rollback(false)
	public void initCompany(){
		Company company = new Company();
		company.setName("测试公司");
		company.setAddress("测试地址");
		companyDao.save(company);
		
		Account account = new Account();
		account.setCompany(company);
		account.setName("测试1");
		account.setLoginId("ceshi1");
		account.setPassword("123456");
		//设置密码
		try {
			PasswordEncry.encrypt(account);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		accountDao.save(account);
	}
	
	@Test
	@Rollback(false)
	public void initRoleFunction(){
		Account account = accountDao.findByLoginId("ceshi1");
		Iterable<RoleFunction> roleFunctions = this.roleFunctionDao.findAll();
		for (RoleFunction roleFunction : roleFunctions) {
			Privilege privilege = new Privilege();
			privilege.setAccount(account);
			privilege.setRoleFunction(roleFunction);
			List<RoleFunctionBtn> roleFunctionBtnList = roleFunction.getRoleFunctionBtnList();
			for (RoleFunctionBtn roleFunctionBtn : roleFunctionBtnList) {
				privilege.getRoleFunctionBtnList().add(roleFunctionBtn);
			}
			privilegeDao.save(privilege);
		}
	}
	
	@Test
	@Rollback(false)
	public void initCompanyCode(){
		Iterable<Company> companys = this.companyDao.findAll();
		for (Company company : companys) {
			company.setCode(UUID.randomUUID().toString().replace("-", ""));
		}
	}
	
}
