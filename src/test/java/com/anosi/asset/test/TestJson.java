package com.anosi.asset.test;

import java.util.Date;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TestJson {

	@Test
	public void testParseToBean(){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("serial_no", "abc123");
		jsonObject.put("openTime", new Date());
		jsonObject.put("companyName", "123");
		/*Iotx iotx = JSON.parseObject(jsonObject.toString(), Iotx.class);
		System.out.println(iotx.getSerialNo());*/
		JSONObject parseObject = JSON.parseObject(jsonObject.toString());
		System.out.println(new Date((long) parseObject.get("openTime")));
	}
	
}
