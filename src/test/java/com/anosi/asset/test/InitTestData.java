package com.anosi.asset.test;

import java.util.Date;
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
import com.anosi.asset.dao.mongo.IotxDataDao;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.AlarmData;
import com.anosi.asset.model.jpa.AlarmData.Level;
import com.anosi.asset.model.jpa.Company;
import com.anosi.asset.model.jpa.Device;
import com.anosi.asset.model.jpa.Dust;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.Privilege;
import com.anosi.asset.model.jpa.Role;
import com.anosi.asset.model.jpa.RoleFunction;
import com.anosi.asset.model.jpa.RoleFunctionBtn;
import com.anosi.asset.model.jpa.Sensor;
import com.anosi.asset.model.jpa.Iotx.NetworkCategory;
import com.anosi.asset.model.jpa.Iotx.Status;
import com.anosi.asset.model.mongo.IotxData;
import com.anosi.asset.service.AlarmDataService;
import com.anosi.asset.service.CompanyService;
import com.anosi.asset.service.DeviceService;
import com.anosi.asset.service.DustService;
import com.anosi.asset.service.IotxDataService;
import com.anosi.asset.service.IotxService;
import com.anosi.asset.service.SensorService;

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
	private IotxDataDao iotxDataDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private RoleFunctionDao roleFunctionDao;
	@Autowired
	private PrivilegeDao privilegeDao;
	@Autowired
	private IotxService iotxService;
	@Autowired
	private DustService dustService;
	@Autowired
	private SensorService sensorService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DeviceService deivceService;
	@Autowired
	private IotxDataService iotxDataService;
	@Autowired
	private AlarmDataService alarmDataService;

	@Test
	@Rollback(false)
	public void saveIotxData() {
		IotxData iotxData = new IotxData();
		iotxData.setSensorSN("abc123");
		iotxDataDao.save(iotxData);
	}

	@Test
	@Rollback(false)
	public void initAccounAndRole() {
		Account account = accountDao.findByLoginId("admin");
		for (int i = 1; i < 5; i++) {
			Role role = new Role();
			role.setRoleCode("testcode" + i);
			roleDao.save(role);
			account.getRoleList().add(role);
		}
	}

	@Test
	public void testMerge() {
		Account account = new Account();
		account.setId((long) 1);
		account.setName("hello");
		accountDao.save(account);
		logger.debug(account.getPassword());
		logger.debug(account.getName());
	}

	@Test
	@Rollback(false)
	public void testFlush() {
		Account account = accountDao.findByLoginId("admin");
		account.setName("123");
	}

	@Test
	@Rollback(false)
	public void initCompany() {
		Company company = new Company();
		company.setName("测试公司");
		company.setAddress("测试地址");
		companyDao.save(company);

		Account account = new Account();
		account.setCompany(company);
		account.setName("测试1");
		account.setLoginId("ceshi1");
		account.setPassword("123456");
		// 设置密码
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
	public void initRoleFunction() {
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
	public void initIotx() {
		Company anosi = companyService.findByName("北京安诺信通信科技有限公司");
		Device device = new Device();
		device.setCompany(anosi);
		device.setSerialNo(UUID.randomUUID().toString());
		deivceService.save(device);

		Iotx iotx = new Iotx();
		iotx.setCompany(anosi);
		iotx.setSerialNo(UUID.randomUUID().toString());
		iotx.setLongitude(114.3118287971);
		iotx.setLatitude(30.5984342798);
		iotx.setNetworkCategory(NetworkCategory.WIFI);
		iotx.setStatus(Status.ONLINE);
		iotx.setOpenTime(new Date());
		iotxService.save(iotx);

		Dust dust1 = new Dust();
		dust1.setName("测试微尘1");
		dust1.setSerialNo(UUID.randomUUID().toString());
		dust1.setIotx(iotx);
		dust1.setFrequency((double) 1);
		dust1.setIsWorked(true);
		dust1.setType("486");
		dust1.setPowerType("电源");
		dust1.setConfigId("1");
		dust1.setDevice(device);
		dustService.save(dust1);

		Dust dust2 = new Dust();
		dust2.setName("测试微尘2");
		dust2.setSerialNo(UUID.randomUUID().toString());
		dust2.setIotx(iotx);
		dust2.setFrequency((double) 1);
		dust2.setIsWorked(true);
		dust2.setType("486");
		dust2.setPowerType("电源");
		dust2.setConfigId("2");
		dust2.setDevice(device);
		dustService.save(dust2);

		Sensor sensor1 = new Sensor();
		sensor1.setSerialNo(UUID.randomUUID().toString());
		sensor1.setDust(dust1);
		sensor1.setIsWorked(true);
		sensor1.setMaxVal((double) 50);
		sensor1.setMinVal((double) 20);
		sensorService.save(sensor1);

		Sensor sensor2 = new Sensor();
		sensor2.setSerialNo(UUID.randomUUID().toString());
		sensor2.setDust(dust1);
		sensor2.setIsWorked(true);
		sensor2.setMaxVal((double) 60);
		sensor2.setMinVal((double) 30);
		sensorService.save(sensor2);
	}

	@Test
	@Rollback(false)
	public void initIotxData() {
		Sensor sensor = sensorService.getOne((long) 1);

		IotxData iotxData = new IotxData();
		iotxData = setCommonValue(iotxData, sensor);
		iotxData.setVal((double) 30);
		iotxDataService.save(iotxData);
		
		IotxData iotxData2 = new IotxData();
		iotxData2 = setCommonValue(iotxData2, sensor);
		iotxData2.setVal((double) 60);
		iotxData2.setMessage("发生一级告警");
		iotxDataService.save(iotxData2);
		
		AlarmData alarmData = new AlarmData();
		alarmData.setSensor(sensor);
		alarmData.setVal(iotxData2.getVal());
		alarmData.setCollectTime(iotxData2.getCollectTime());
		alarmData.setLevel(Level.ALARM_1);
		alarmDataService.save(alarmData);
		
		IotxData iotxData3 = new IotxData();
		iotxData3 = setCommonValue(iotxData3, sensor);
		iotxData3.setVal((double) 100);
		iotxData3.setMessage("发生二级告警");
		iotxDataService.save(iotxData3);
		
		AlarmData alarmData2 = new AlarmData();
		alarmData2.setSensor(sensor);
		alarmData2.setVal(iotxData3.getVal());
		alarmData2.setCollectTime(iotxData3.getCollectTime());
		alarmData2.setLevel(Level.ALARM_2);
		alarmDataService.save(alarmData2);
	}

	private IotxData setCommonValue(IotxData iotxData, Sensor sensor) {
		iotxData.setSensorSN(sensor.getSerialNo());
		iotxData.setCollectTime(new Date());
		return iotxData;
	}

}
