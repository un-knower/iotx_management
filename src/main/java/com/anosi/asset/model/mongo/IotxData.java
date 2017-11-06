package com.anosi.asset.model.mongo;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.alibaba.fastjson.annotation.JSONField;

@Document
public class IotxData extends AbstractDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1976199296388056305L;

	@Indexed
	private String sensorSN;

	private Double val;

	private Date collectTime;

	private String message;

	public String getSensorSN() {
		return sensorSN;
	}

	public void setSensorSN(String sensorSN) {
		this.sensorSN = sensorSN;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Double getVal() {
		return val;
	}

	public void setVal(Double val) {
		this.val = val;
	}

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	public Date getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(Date collectTime) {
		this.collectTime = collectTime;
	}

}
