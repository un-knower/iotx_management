package com.anosi.asset.test;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.IotxManagementApplication;
import com.anosi.asset.mqtt.MqttServer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IotxManagementApplication.class)
@Transactional
public class MqttTest {
	
	@Autowired
	private MqttServer mqttServer;

	@Test
	public void testSend() throws Exception {
		MqttMessage message = new MqttMessage();
		message.setQos(2);
		message.setRetained(true);
		message.setPayload("给客户端129推送的信息".getBytes());
		mqttServer.publish("toclient/124", message);
	}
	
}
