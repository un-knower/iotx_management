package com.anosi.asset.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="sensorInterface")
public class SensorInterface extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -988482698467271788L;

	private String name;
	
	private List<SensorPort> sensorPortList = new ArrayList<SensorPort>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(cascade=CascadeType.MERGE,fetch=FetchType.LAZY,mappedBy="sensorInterface",targetEntity=SensorPort.class)
	public List<SensorPort> getSensorPortList() {
		return sensorPortList;
	}

	public void setSensorPortList(List<SensorPort> sensorPortList) {
		this.sensorPortList = sensorPortList;
	}
	
}
