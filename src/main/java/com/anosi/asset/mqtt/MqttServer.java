package com.anosi.asset.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class MqttServer {

	private static final Logger logger = LoggerFactory.getLogger(MqttServer.class);

	@Value("${mqtt.serverURIs}")
	private String serverURIs;

	@Value("${mqtt.userName}")
	private String userName;

	@Value("${mqtt.password}")
	private String password;

	@Value("${mqtt.clientId}")
	private String clientId;

	private MqttClient client;

	public MqttServer(String serverURIs, String userName, String password, String clientId) throws MqttException {
		this.serverURIs = serverURIs;
		this.userName = userName;
		this.password = password;
		this.clientId = clientId;
		// MemoryPersistence设置clientid的保存形式，默认为以内存保存
		client = new MqttClient(serverURIs, clientId, new MemoryPersistence());
		connect();
	}

	private void connect() {
		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(false);
		options.setUserName(userName);
		options.setPassword(password.toCharArray());
		// 设置超时时间
		options.setConnectionTimeout(10);
		// 设置会话心跳时间
		options.setKeepAliveInterval(20);
		try {
			client.setCallback(new MqttCallback() {

				public void messageArrived(String topic, MqttMessage message) throws Exception {
					// subscribe后得到的消息会执行到这里面
				}

				public void deliveryComplete(IMqttDeliveryToken token) {
					logger.debug("deliveryComplete---------" + token.getMessageId());
				}

				public void connectionLost(Throwable cause) {
					// 连接丢失后，一般在这里面进行重连
				}
			});
			client.connect(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 发送消息
	 * 
	 * @param topicName
	 * @param message
	 * @throws MqttPersistenceException
	 * @throws MqttException
	 */
	public void publish(String topicName, MqttMessage message) throws MqttPersistenceException, MqttException {
		MqttDeliveryToken token = client.getTopic(topicName).publish(message);
		token.waitForCompletion();
		logger.debug("message is published completely! " + token.isComplete());
	}

}
