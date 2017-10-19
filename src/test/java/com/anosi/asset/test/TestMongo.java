package com.anosi.asset.test;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.IotxManagementApplication;
import com.google.common.collect.ImmutableMap;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IotxManagementApplication.class)
@Transactional
public class TestMongo {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Test
	public void testMongo(){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("header", new JSONObject(ImmutableMap.of("uniqueId",UUID.randomUUID().toString(),"type", "sensor")));
		mongoTemplate.save(jsonObject, "mqttSend");
	}
	
}
