package com.anosi.asset.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
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

	@Autowired
	private MessageHandler messageHandler;

	@Autowired
	private SubscribeComponent subscribeComponent;

	private MqttClient client;

	private MqttConnectOptions options = new MqttConnectOptions();

	public void connect() throws MqttSecurityException, MqttException {
		if (client != null && client.isConnected()) {
			return;
		}
		client = new MqttClient(this.serverURIs, this.clientId, new MemoryPersistence());
		options.setCleanSession(false);
		options.setUserName(userName);
		options.setPassword(password.toCharArray());
		// 设置超时时间
		options.setConnectionTimeout(10);
		// 设置会话心跳时间
		options.setKeepAliveInterval(20);
		client.setCallback(new MqttCallbackExtended() {

			@Override
			public void connectComplete(boolean reconnect, String serverURI) {
				logger.debug("connect success");
				MqttMessage message = new MqttMessage();
				message.setQos(2);
				message.setRetained(true);
				message.setPayload("server is connected".getBytes());
				try {
					publish("/server/status", message);
				} catch (MqttPersistenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public void messageArrived(String topic, MqttMessage message) throws Exception {
				// subscribe后得到的消息会执行到这里面
				messageHandler.handleMessage(topic, message);
			}

			public void deliveryComplete(IMqttDeliveryToken token) {
				logger.debug("deliveryComplete---------" + token.getMessageId());
			}

			public void connectionLost(Throwable cause) {
				// 连接丢失后
			}

		});
		// 设置遗言，在连接断开时发送
		options.setWill("/server/status", "server is closed".getBytes(), 2, true);
		subscribeComponent.setSubscribe();
		client.connect(options);
		client.subscribe(subscribeComponent.getSubscrides().getTopics(), subscribeComponent.getSubscrides().getQos());
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

	/***
	 * 添加新的订阅
	 * 
	 * @param topicName
	 * @param qos
	 * @throws MqttException
	 */
	public void subscribeNewTopic(String topicName[], int[] qos) throws MqttException {
		subscribeComponent.addSubscribe(topicName, qos);
		client.subscribe(subscribeComponent.getSubscrides().getTopics(), subscribeComponent.getSubscrides().getQos());
	}

}
