package com.anosi.asset.mqtt;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.anosi.asset.component.I18nComponent;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.jpa.Dust;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.Iotx.Status;
import com.anosi.asset.model.mongo.Message;
import com.anosi.asset.model.mongo.Message.Type;
import com.anosi.asset.model.jpa.Sensor;
import com.anosi.asset.service.DustService;
import com.anosi.asset.service.IotxRemoteService;
import com.anosi.asset.service.IotxService;
import com.anosi.asset.service.MessageService;
import com.anosi.asset.service.SensorService;

/***
 * 消息的处理类
 * 
 * @author jinyao
 *
 */
@Component
@Transactional
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
	@Autowired
	private I18nComponent i18nComponent;
	@Autowired
	private MessageService messageService;

	/***
	 * 进行消息是否处理过的校验
	 * 
	 * @param topic
	 * @param message
	 */
	public void handleMessage(String topic, MqttMessage message) {
		ValueOperations<String, String> opv = redisTemplate.opsForValue();
		String lastValue = opv.get(topic);
		// 没有处理过的话，再进行处理
		if (!Objects.equals(new String(message.getPayload()), lastValue)) {
			try {
				handle(topic, message);
				// 最后把本次处理add到redis中
				opv.set(topic, new String(message.getPayload()));
			} catch (Exception e) {
				e.printStackTrace();
				throw new CustomRunTimeException(i18nComponent.getMessage("mqtt.message.handle.fail"));
			}
		} else {
			// 处理过的话直接返回
			return;
		}
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
		switch (topic) {
		case "configure/callBack":
			handleConfigureCallBack(topic, message);
			break;
		case "configure/status":
			handleIotxStatus(topic, message);
			break;
		case "configure/sensor":
			handleConfigureSensor(topic, message);
			break;
		default:
			break;
		}
	}

	/***
	 * 远程配置的callback。sensor或者dust离线(上线)也可以通过向这个topic发送消息来设置
	 * 
	 * @param topic
	 * @param message
	 *            内容格式:{header:{type:xxx,serialNo:xxx},body:{k1:v1,k2:v2}}
	 * @throws Exception
	 */
	private void handleConfigureCallBack(String topic, MqttMessage message) throws Exception {
		// 将接收的消息持久化到mongodb
		Message payLoad = JSON.parseObject(new String(message.getPayload()), Message.class);
		payLoad.setType(Type.RECEIVE);
		messageService.save(payLoad);
		
		Boolean status = payLoad.getResponse().getStatus();
		if (!status) {
			// TODO 错误处理
			return;
		}

		// 获取type和val
		Map<String, Object> values = new HashMap<>();
		values.put(payLoad.getBody().getType(), payLoad.getBody().getValue());

		String serialNo = payLoad.getHeader().getSerialNo();
		switch (payLoad.getHeader().getType()) {
		case "iotx":
			Iotx iotx = iotxService.findBySerialNo(serialNo);
			iotxRemoteService.setValue(iotx, values);
			break;
		case "dust":
			Dust dust = dustService.findBySerialNo(serialNo);
			// 如果是新增dust
			if (dust == null) {
				dust = new Dust();
			}
			iotxRemoteService.setValue(dust, values);
			dustService.save(dust);
			break;
		case "sensor":
			Sensor sensor = sensorService.findBySerialNo(serialNo);
			// 如果是新增sensor
			if (sensor == null) {
				sensor = new Sensor();
			}
			iotxRemoteService.setValue(sensor, values);
			if (values.containsKey("frequency")) {
				sensor.getDust().setFrequency((Double) values.get("frequency"));
			}
			sensorService.save(sensor);
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
	private void handleIotxStatus(String topic, MqttMessage message) {
		// 将接收的消息持久化到mongodb
		Message payLoad = JSON.parseObject(new String(message.getPayload()), Message.class);
		payLoad.setType(Type.RECEIVE);
		messageService.save(payLoad);
		
		String status = payLoad.getBody().getValue().toString();
		String serialNo = payLoad.getHeader().getSerialNo();
		Iotx iotx = iotxService.findBySerialNo(serialNo);
		if ("offline".equals(status)) {
			iotx.setStatus(Status.OFFLINE);
		} else if ("online".equals(status)) {
			iotx.setStatus(Status.ONLINE);
		}
	}
	
	/***
	 * 处理传感器元数据
	 * 
	 * @param topic
	 * @param message
	 */
	private void handleConfigureSensor(String topic, MqttMessage message) {
		
	}

}
