package com.anosi.asset.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfiguration {

	@Value("${mqtt.serverURIs}")
	private String serverURIs;

	@Value("${mqtt.userName}")
	private String userName;

	@Value("${mqtt.password}")
	private String password;

	@Value("${mqtt.clientId}")
	private String clientId;

	@Bean
	public MqttServer mqttServer() throws MqttException {
		return new MqttServer(serverURIs, userName, password, clientId);
	}

}
