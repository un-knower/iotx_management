package com.anosi.asset.mqtt;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.jpa.Dust;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.Iotx.Status;
import com.anosi.asset.model.jpa.Sensor;
import com.anosi.asset.service.DustService;
import com.anosi.asset.service.IotxRemoteService;
import com.anosi.asset.service.IotxService;
import com.anosi.asset.service.SensorService;

/***
 * 消息的处理类
 * 
 * @author jinyao
 *
 */
@Component
public class MessageHandler {

	// key为topic,value为message,每次处理message前要判断是否处理过
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private IotxService iotxService;
	@Autowired
	private DustService dustService;
	@Autowired
	private SensorService sensorService;
	@Autowired
	private IotxRemoteService iotxRemoteService;

	/***
	 * 进行消息是否处理过的校验
	 * 
	 * @param topic
	 * @param message
	 * @throws Exception
	 */
	public void handleMessage(String topic, MqttMessage message) throws Exception {
		ValueOperations<String, String> opv = redisTemplate.opsForValue();
		String lastValue = opv.get(topic);
		// 没有处理过的话，再进行处理
		if (!Objects.equals(new String(message.getPayload()), lastValue)) {
			handle(topic, message);
		} else {
			// 处理过的话直接返回
			return;
		}
		// 最后把本次处理add到redis中
		opv.set(topic, new String(message.getPayload()));
	}

	/***
	 * 具体处理消息
	 * 
	 * @param topic
	 * @param message
	 * @throws Exception
	 */
	private void handle(String topic, MqttMessage message) throws Exception {
		// 按照topic分别处理消息
		Method handle = this.getClass().getMethod("handle" + topic.substring(0, 1).toUpperCase() + topic.substring(1),
				String.class, MqttMessage.class);
		handle.invoke(this, topic, message);
	}

	/***
	 * 远程配置的callback。sensor或者dust离线(上线)也可以通过向这个topic发送消息来设置
	 * 
	 * @param topic
	 * @param message
	 *            内容格式:{header:{type:xxx,serialNo:xxx},body:{k1:v1,k2:v2}}
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void handleConfigureCallback(String topic, MqttMessage message) throws Exception {
		JSONObject jsonMessage = JSON.parseObject(new String(message.getPayload()));
		JSONObject header = jsonMessage.getJSONObject("header");
		JSONObject body = jsonMessage.getJSONObject("body");
		Map<String, Object> values = JSON.parseObject(body.toString());
		String serialNo = header.getString("serialNo");
		switch (header.getString("type")) {
		case "iotx":
			Iotx iotx = iotxService.findBySerialNo(serialNo);
			iotxRemoteService.setValue(iotx, values);
			break;
		case "dust":
			Dust dust = dustService.findBySerialNo(serialNo);
			iotxRemoteService.setValue(dust, values);
			break;
		case "sensor":
			Sensor sensor = sensorService.findBySerialNo(serialNo);
			iotxRemoteService.setValue(sensor, values);
			break;
		default:
			break;
		}
	}

	/***
	 * 根据消息来设置iotx是否在线
	 * 
	 * @param topic
	 * @param message
	 *            内容格式:{header:{serialNo:xxx},body:{status:xxx}}
	 */
	@SuppressWarnings("unused")
	private void handleStatus(String topic, MqttMessage message) {
		JSONObject jsonMessage = JSON.parseObject(new String(message.getPayload()));
		JSONObject header = jsonMessage.getJSONObject("header");
		JSONObject body = jsonMessage.getJSONObject("body");
		String status = body.getString("status");
		String serialNo = header.getString("serialNo");
		Iotx iotx = iotxService.findBySerialNo(serialNo);
		if ("offline".equals(status)) {
			iotx.setStatus(Status.OFFLINE);
		} else if ("online".equals(status)) {
			iotx.setStatus(Status.ONLINE);
		}
	}

}
