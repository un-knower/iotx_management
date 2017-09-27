package com.anosi.asset.model.mongo;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.alibaba.fastjson.annotation.JSONField;
import com.anosi.asset.model.elasticsearch.Content;

@Document
public class IotxData extends AbstractDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1976199296388056305L;

	@Content
	@Indexed
	private String iotxSN;

	@Content
	@Indexed
	private String dustSN;

	@Content
	@Indexed
	private String sensorSN;

	@Content
	@Indexed
	private String deviceSN;

	@Content
	@Indexed
	private String companyName;

	private Double val;

	private String unit;// 单位

	private Double maxVal;

	private Double minVal;

	@Content
	private Date collectTime;

	private Date closeTime;

	@Content
	private String message;

	@Content(extractFields = { "level.level" })
	private Level level;

	@Content
	private String category;

	private boolean alarm = false;

	private Double baiduLongitude;// 经度

	private Double baiduLatitude;// 纬度

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

	public String getDustSN() {
		return dustSN;
	}

	public void setDustSN(String dustSN) {
		this.dustSN = dustSN;
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


	public Double getBaiduLongitude() {
		return baiduLongitude;
	}

	public void setBaiduLongitude(Double baiduLongitude) {
		this.baiduLongitude = baiduLongitude;
	}

	public Double getBaiduLatitude() {
		return baiduLatitude;
	}

	public void setBaiduLatitude(Double baiduLatitude) {
		this.baiduLatitude = baiduLatitude;
	}

	public boolean isAlarm() {
		return alarm;
	}

	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}
	
}
