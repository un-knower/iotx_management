package com.anosi.asset.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.IotxManagementApplication;
import com.anosi.asset.dao.jpa.IotxDao;
import com.anosi.asset.model.jpa.Iotx;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IotxManagementApplication.class)
@Transactional
public class CrudTest {
	
	@Autowired
	private IotxDao iotxDao;
	
	@Test
	private void testMerge(){
		Iotx iotx = new Iotx();
		iotx.setId((long) 1);
		iotx.setSerialNo("ABC123");
		iotxDao.save(iotx);
	}
	
}
