package com.anosi.asset.mqtt;

import java.util.Objects;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

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

	public void handleMessage(String topic, MqttMessage message) {
		ValueOperations<String, String> opv = redisTemplate.opsForValue();
		String lastValue = opv.get(topic);
		// 没有处理过的话，再进行处理
		if (!Objects.equals(new String(message.getPayload()), lastValue)) {
			
		} else {
			// 处理过的话直接返回
			return;
		}
		// 最后把本次处理add到redis中
		opv.set(topic, new String(message.getPayload()));
	}

}
