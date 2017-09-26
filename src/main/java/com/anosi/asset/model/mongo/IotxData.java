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
	private String iotxSN;

	@Indexed
	private String sensorSN;
	
	@Indexed
	private String deviceSN;

	@Indexed
	private String companyName;

	private Double val;

	private String unit;// 单位

	private Double maxVal;

	private Double minVal;

	private Date collectTime;

	private Date closeTime;

	private String message;

	private Level level;
	
	private String category;

	// 内部枚举类
	public static enum Level {
		NORMAL("正常"), ALARM_1("一级告警"), ALARM_2("二级告警"), ALARM_3("三级告警");

		private String level;

		private Level(String level) {
			this.level = level;
		}

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}
	}
	
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getIotxSN() {
		return iotxSN;
	}

	public void setIotxSN(String iotxSN) {
		this.iotxSN = iotxSN;
	}

	public String getSensorSN() {
		return sensorSN;
	}

	public void setSensorSN(String sensorSN) {
		this.sensorSN = sensorSN;
	}

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	public Date getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public Double getVal() {
		return val;
	}

	public void setVal(Double val) {
		this.val = val;
	}

	public Double getMaxVal() {
		return maxVal;
	}

	public void setMaxVal(Double maxVal) {
		this.maxVal = maxVal;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Double getMinVal() {
		return minVal;
	}

	public void setMinVal(Double minVal) {
		this.minVal = minVal;
	}

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	public Date getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(Date collectTime) {
		this.collectTime = collectTime;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDeviceSN() {
		return deviceSN;
	}

	public void setDeviceSN(String deviceSN) {
		this.deviceSN = deviceSN;
	}
	
}
