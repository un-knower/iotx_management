package com.anosi.asset.model.jpa;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "alarmData")
public class AlarmData extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2809041465873879025L;

	private Sensor sensor;

	private Double val;

	private Date collectTime;

	private Date closeTime;

	private Level level;
	
	public AlarmData() {
		super();
	}
	
	public AlarmData(Sensor sensor, Double val, Date collectTime, Level level) {
		super();
		this.sensor = sensor;
		this.val = val;
		this.collectTime = collectTime;
		this.level = level;
	}

	// 内部枚举类
	// 跳闸 报警 提示
	public static enum Level {
		NORMAL, TRIP, ALARM, REMIND
	}

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Sensor.class)
	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	public Double getVal() {
		return val;
	}

	public void setVal(Double val) {
		this.val = val;
	}

	public Date getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(Date collectTime) {
		this.collectTime = collectTime;
	}

	public Date getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
	
}
