package com.anosi.asset.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.anosi.asset.model.elasticsearch.Content;

@Entity
@Table(name = "dust")
@Indexed
@Analyzer(impl = IKAnalyzer.class)
public class Dust extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4198486870068912965L;

	@Field
	@Content
	private String name;

	@Field
	@Content
	private String serialNo;

	private Double frequency;// 采集频率，单位:秒
	
	private Long sensorQuantity;

	@Field
	@Content
	private String type;

	@Field
	@Content
	private String powerType;

	private String configId;

	@IndexedEmbedded
	private Iotx iotx;

	@ContainedIn
	private List<Sensor> sensorList = new ArrayList<>();
	
	private Device device;
	
	private Boolean isWorked = false;
	
	private Long alarmQuantity;
	
	private Long unConfirmAlarmQuantity;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(unique = true, nullable = false)
	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public Double getFrequency() {
		return frequency;
	}

	public void setFrequency(Double frequency) {
		this.frequency = frequency;
	}

	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "dust", targetEntity = Sensor.class)
	public List<Sensor> getSensorList() {
		return sensorList;
	}

	public void setSensorList(List<Sensor> sensorList) {
		this.sensorList = sensorList;
	}

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Iotx.class)
	public Iotx getIotx() {
		return iotx;
	}

	public void setIotx(Iotx iotx) {
		this.iotx = iotx;
	}
	
	// 获取关联的传感器数量
	@Formula("(select COUNT(*) from sensor s  where s.dust_id=id)")
	public Long getSensorQuantity() {
		return sensorQuantity;
	}

	public void setSensorQuantity(Long sensorQuantity) {
		this.sensorQuantity = sensorQuantity;
	}
	
	@Formula("(select COUNT(*) from alarm_data a left join sensor s on a.sensor_id=s.id where s.dust_id=id)")
	public Long getAlarmQuantity() {
		return alarmQuantity;
	}

	public void setAlarmQuantity(Long alarmQuantity) {
		this.alarmQuantity = alarmQuantity;
	}
	
	@Formula("(select COUNT(*) from alarm_data a left join sensor s on a.sensor_id=s.id where s.dust_id=id and a.collect_time is null)")
	public Long getUnConfirmAlarmQuantity() {
		return unConfirmAlarmQuantity;
	}

	public void setUnConfirmAlarmQuantity(Long unConfirmAlarmQuantity) {
		this.unConfirmAlarmQuantity = unConfirmAlarmQuantity;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPowerType() {
		return powerType;
	}

	public void setPowerType(String powerType) {
		this.powerType = powerType;
	}

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}
	
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Device.class)
	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Boolean getIsWorked() {
		return isWorked;
	}

	public void setIsWorked(Boolean isWorked) {
		this.isWorked = isWorked;
	}
	
}
