package com.anosi.asset.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.IotxManagementApplication;
import com.anosi.asset.model.jpa.Sensor;
import com.anosi.asset.service.AccountService;
import com.anosi.asset.service.SensorService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IotxManagementApplication.class)
@Transactional
public class CacheTest {

	private static final Logger logger = LoggerFactory.getLogger(CacheTest.class);
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private SensorService sensorService;
	
	@Test
	public void testCacheByFindAccount(){
		logger.debug("this is first query");
		accountService.findByLoginId("admin");
		logger.debug("this is second query");
		accountService.findByLoginId("admin");
	}
	
	@Test
	@Cacheable(value="findBySerialNo")
	public void testLazy(){
		Sensor sensor = sensorService.findBySerialNo("abc123");
		logger.debug(sensor.getSensorCategory().getName());
	}
	
	@Test
	public void testLazyEntity(){
		Sensor sensor = sensorService.findBySerialNo("abc123");
		logger.debug(sensor.getSensorCategory().getName());
	}
	
	@Test
	public void testEvictCache(){
		logger.debug("cacheEvict");
	}
	
}
