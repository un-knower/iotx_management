package com.anosi.asset.mqtt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.anosi.asset.util.FormatUtil;

/***
 * 消息的处理类
 * 
 * @author jinyao
 *
 */
@Component
public class MessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

	@Autowired
	private IotxService iotxService;
	@Autowired
	private DustService dustService;
	@Autowired
	private SensorService sensorService;
	@Autowired
	private IotxRemoteService iotxRemoteService;
	@Autowired
	private MessageService messageService;

	/***
	 * 进行消息是否处理过的校验
	 * 
	 * @param topic
	 * @param message
	 */
	@Transactional
	public void handleMessage(String topic, MqttMessage message) {
		try {
			logger.debug("reveice mqtt message,topic:{},message:{}", topic, message.getPayload());
			handle(topic, message);
		} catch (Exception e) {
			e.printStackTrace();
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
		case "iotx/usedPercent":
			handleUIotxUsedPercent(topic, message);
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
		payLoad.setTopic(topic);
		messageService.save(payLoad);

		Boolean status = payLoad.getResponse().getStatus();
		if (!status) {
			// TODO 错误处理
			return;
		}

		// 获取type和val
		Map<String, Object> values = new HashMap<>();
		values.put(payLoad.getBody().getType(), payLoad.getBody().getVal());

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
			iotxRemoteService.setValue(sensor, values);
			if (values.containsKey("job_time")) {
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
		payLoad.setTopic(topic);
		messageService.save(payLoad);

		String status = payLoad.getBody().getVal().toString();
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
	 * @throws Exception
	 */
	private void handleConfigureSensor(String topic, MqttMessage message) throws Exception {
		List<Message> payLoads = JSON.parseArray(new String(message.getPayload()), Message.class);
		List<Sensor> sensors = payLoads.parallelStream().map(payLoad -> {
			payLoad.setType(Type.RECEIVE);
			payLoad.setTopic(topic);
			// 将message中body部分的meta转换成map
			return convertSensor(payLoad,
					new HashMap<>(JSON.parseObject(JSON.toJSONString(payLoad.getBody().getVal()))));
		}).collect(Collectors.toList());
		messageService.save(payLoads);
		sensorService.save(sensors);
	}

	/***
	 * 解析成sensor
	 * 
	 * @param payLoad
	 * @param values
	 * @return
	 */
	private Sensor convertSensor(Message payLoad, Map<String, Object> values) {
		return sensorService.convertSensor(payLoad, values);
	}

	/***
	 * 解析iotx内存CPU硬盘的使用百分比
	 * 
	 * @param topic
	 * @param message
	 *            内容格式:{header:{serialNo:xxx},body:{type:xxx,val:{usedMemoryPer:
	 *            0.5,usedHardDiskPer:0.5,usedCpuPer:0.5}}}
	 */
	private void handleUIotxUsedPercent(String topic, MqttMessage message) {
		// 将接收的消息持久化到mongodb
		Message payLoad = JSON.parseObject(new String(message.getPayload()), Message.class);
		payLoad.setType(Type.RECEIVE);
		payLoad.setTopic(topic);
		messageService.save(payLoad);

		String serialNo = payLoad.getHeader().getSerialNo();
		Iotx iotx = iotxService.findBySerialNo(serialNo);
		JSONObject json = JSON.parseObject(JSON.toJSONString(payLoad.getBody().getVal()));
		iotx.setUsedCpuPer(FormatUtil.convertPerCent2Double(json.getString("usedCpuPer")));
		iotx.setUsedHardDiskPer(FormatUtil.convertPerCent2Double(json.getString("usedHardDiskPer")));
		iotx.setUsedMemoryPer(FormatUtil.convertPerCent2Double(json.getString("usedMemoryPer")));
		iotxService.save(iotx);
	}

}
