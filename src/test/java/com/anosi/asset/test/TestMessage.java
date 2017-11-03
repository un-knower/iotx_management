package com.anosi.asset.test;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.anosi.asset.model.mongo.Message;
import com.anosi.asset.model.mongo.Message.Body;
import com.anosi.asset.model.mongo.Message.Header;
import com.anosi.asset.model.mongo.Message.Response;
import com.anosi.asset.model.mongo.Message.Type;

public class TestMessage {
	
	@Test
	public void testJson(){
		Message message = new Message();
		Header header = new Header();
		Body body = new Body();
		Response response = new Response();
		header.setType("iotx");
		header.setSerialNo("abc123");
		message.setHeader(header);
		body.setType("value");
		body.setVal("1.0");
		message.setBody(body);
		response.setStatus(true);
		message.setResponse(response);
		message.setType(Type.SEND);
		String jsonString = JSON.toJSONString(message);
		System.out.println(jsonString);
		Message parse = JSON.parseObject(jsonString, Message.class);
		System.out.println(parse.getType());
	}
	
}
