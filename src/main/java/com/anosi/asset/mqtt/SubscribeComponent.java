package com.anosi.asset.mqtt;

import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

/***
 * 设置订阅的类
 * 
 * @author jinyao
 *
 */
@Component
public class SubscribeComponent {

	private Subscrides subscrides = new Subscrides();

	private List<String> topicList = new Vector<>();// vector线程安全

	private List<Integer> qosList = new Vector<>();

	/***
	 * 设置所有订阅
	 */
	public void setSubscribe() {
		// 设置订阅
		setIotxSubscrides();
		convert();
	}

	/***
	 * 添加订阅
	 * 
	 * @param topicName
	 * @param qos
	 * @return
	 */
	public SubscribeComponent addSubscribe(String[] topicName, int[] qos) {
		for (String topic : topicName) {
			topicList.add(topic);
		}
		for (int i : qos) {
			qosList.add(i);
		}
		convert();
		return this;
	}

	/***
	 * list转array
	 */
	private void convert() {
		String[] topics = new String[topicList.size()];
		subscrides.setTopics(topicList.toArray(topics));
		Integer[] qoss = new Integer[qosList.size()];
		subscrides.setQos(ArrayUtils.toPrimitive(qosList.toArray(qoss)));
	}

	/***
	 * 对每个iotx设置以下订阅:
	 * 
	 * <pre>
	 * 	远程配置iotx、dust、sensor后，收到callback回应 会发送给以下topic,Qos都为2:
	 * 
	 * <pre>
	 *	/configureCallback;
	 * </pre>
	 * 
	 * 在iotx或dust或sensor在线或离线后，收到callback回应 会发送给以下topic,Qos都为2:
	 * 
	 * <pre>
	 * 	/status;
	 * </pre>
	 * </pre>
	 * 
	 */
	private void setIotxSubscrides() {
		topicList.add("/configureCallback");
		qosList.add(2);
		topicList.add("/status");
		qosList.add(2);
	}

	public Subscrides getSubscrides() {
		return subscrides;
	}

	public void setSubscrides(Subscrides subscrides) {
		this.subscrides = subscrides;
	}

	class Subscrides {

		private String[] topics;

		private int[] qos;

		public String[] getTopics() {
			return topics;
		}

		public void setTopics(String[] topics) {
			this.topics = topics;
		}

		public int[] getQos() {
			return qos;
		}

		public void setQos(int[] qos) {
			this.qos = qos;
		}

	}
}
