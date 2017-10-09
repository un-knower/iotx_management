package com.anosi.asset.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.IotxManagementApplication;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.service.IotxService;
import com.anosi.asset.util.DateFormatUtil;
import com.anosi.asset.util.MapUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IotxManagementApplication.class)
@Transactional
public class TestMap {

	private static final Logger logger = LoggerFactory.getLogger(TestMap.class);
	
	@Autowired
	private IotxService iotxService;

	@Test
	public void testMapGeoCode() {
		JSONObject jsonObject = MapUtil.geocoderByLocation("116.404", "39.915");
		JSONObject result = jsonObject.getJSONObject("result");
		JSONObject addressComponent = result.getJSONObject("addressComponent");
		logger.debug("addressComponent:{}",addressComponent);
	}
	
	@Test
	@Rollback(false)
	public void testMapDistrict(){
		Iterable<Iotx> iotxs = iotxService.findAll();
		for (Iotx iotx : iotxs) {
			iotxService.setIotxDistrict(iotx);
		}
	}
	
	@Test
	public void testDate(){
		String time = "2015-09-26 00:00:00";
		System.out.println(DateFormatUtil.getDateByParttern(time));
	}

}
