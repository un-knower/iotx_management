package com.anosi.asset.model.mongo;

import java.util.Date;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.alibaba.fastjson.annotation.JSONField;

@Document
@CompoundIndexes({ @CompoundIndex(name = "sensor_time", def = "{'sensorSN' : 1, 'collectTime': 1}", unique = true) })
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

	public IotxData() {
		super();
	}

	public IotxData(String sensorSN, Double val, Date collectTime) {
		super();
		this.sensorSN = sensorSN;
		this.val = val;
		this.collectTime = collectTime;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collectTime == null) ? 0 : collectTime.hashCode());
		result = prime * result + ((sensorSN == null) ? 0 : sensorSN.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IotxData other = (IotxData) obj;
		if (collectTime == null) {
			if (other.collectTime != null)
				return false;
		} else if (!collectTime.equals(other.collectTime))
			return false;
		if (sensorSN == null) {
			if (other.sensorSN != null)
				return false;
		} else if (!sensorSN.equals(other.sensorSN))
			return false;
		return true;
	}
	
}
