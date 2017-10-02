package com.anosi.asset.test;

import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.util.BeanRefUtil;

public class TestBeanRef {

	@Test
	public void testSet() throws Exception{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("serialNo", "abc123");
		jsonObject.put("openTime", new Date());
		jsonObject.put("companyName", "123");
		Map<String,Object> map = JSON.parseObject(jsonObject.toString());
		System.out.println(map);
		Iotx iotx = new Iotx();
		BeanRefUtil.setValue(iotx, map);
		System.out.println(iotx.getOpenTime());
	}
	
}
