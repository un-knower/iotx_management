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

import com.anosi.asset.model.elasticsearch.Content;

@Entity
@Table(name = "dust")
public class Dust extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4198486870068912965L;

	@Content
	private String name;

	@Content
	private String serialNo;

	private Double frequency;// 采集频率，单位:秒

	@Content
	private String type;

	@Content
	private String interfaceName;

	@Content
	private String powerType;

	private String configId;

	private Iotx iotx;

	private List<Sensor> sensorList = new ArrayList<>();

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
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

}
