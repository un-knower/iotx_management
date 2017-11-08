package com.anosi.asset.test;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.Sensor;
import com.anosi.asset.util.BeanRefUtil;

public class TestBeanRef {

	@Test
	public void testSet() throws Exception{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("unique_id", "abc123");
		jsonObject.put("openTime", new Date());
		jsonObject.put("companyName", "123");
		Map<String,Object> map = JSON.parseObject(jsonObject.toString());
		System.out.println(map);
		Iotx iotx = new Iotx();
		BeanRefUtil.setValue(iotx, map);
		System.out.println(iotx.getOpenTime());
		System.out.println(iotx.getSerialNo());
	}
	
	@Test
	public void testSetSensor() throws Exception{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("sensor_name", "abc123");
		jsonObject.put("description", "test");
		jsonObject.put("type", "int");
		jsonObject.put("max_limit", "10.0");
		jsonObject.put("is_used", false);
		Map<String,Object> map = JSON.parseObject(jsonObject.toString());
		System.out.println(map);
		Sensor sensor = new Sensor();
		BeanRefUtil.setValue(sensor, map);
		System.out.println(sensor.getName());
		System.out.println(sensor.getParameterDescribe());
		System.out.println(sensor.getIsWorked());
		System.out.println(sensor.getMaxVal());
	}
	
	@Test
	public void testDate(){
		Calendar ca = Calendar.getInstance();//得到一个Calendar的实例 
		ca.setTime(new Date()); //设置时间为当前时间 
		ca.add(Calendar.DATE, -1); //年份减1 
		Date lastDate = ca.getTime(); //结果
		System.out.println(lastDate);
	}
	
}
