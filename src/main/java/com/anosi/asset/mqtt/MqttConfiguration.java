package com.anosi.asset.mqtt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.stream.CharacterStreamReadingMessageSource;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfiguration {
	
	@Value("${mqtt.serverURIs}")
    private  String serverURIs;
	
    @Value("${mqtt.userName}")
    private  String userName;
    
    @Value("${mqtt.password}")
    private  String password;
    
    @Value("${mqtt.topic}")
    private String topic;

	@Bean
	public MqttPahoClientFactory mqttClientFactory() {
		DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
		factory.setServerURIs(serverURIs);
		factory.setUserName(userName);
		factory.setPassword(password);
		return factory;
	}
	
	
	//下面是一个mqtt的案例，启动项目后，在console界面输入任意字符串，会收到结果
	
	// publisher
	
	@Bean
	public IntegrationFlow mqttOutFlow() {
		return IntegrationFlows.from(CharacterStreamReadingMessageSource.stdin(),
						e -> e.poller(Pollers.fixedDelay(1000)))
				.transform(p -> p + " sent to MQTT")
				.handle(mqttOutbound())
				.get();
	}

	@Bean
	public MessageHandler mqttOutbound() {
		MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("siSamplePublisher", mqttClientFactory());
		messageHandler.setAsync(true);
		messageHandler.setDefaultTopic(topic);
		return messageHandler;
	}
	
	// consumer

	@Bean
	public IntegrationFlow mqttInFlow() {
		return IntegrationFlows.from(mqttInbound())
				.transform(p -> p + ", received from MQTT")
				.handle(logger())
				.get();
	}

	private LoggingHandler logger() {
		LoggingHandler loggingHandler = new LoggingHandler("INFO");
		loggingHandler.setLoggerName("siSample");
		return loggingHandler;
	}

	@Bean
	public MessageProducerSupport mqttInbound() {
		MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("siSampleConsumer",
				mqttClientFactory(), topic);
		adapter.setCompletionTimeout(5000);
		adapter.setConverter(new DefaultPahoMessageConverter());
		adapter.setQos(1);
		return adapter;
	}
	
}
