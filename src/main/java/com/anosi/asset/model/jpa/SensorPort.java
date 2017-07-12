package com.anosi.asset.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="sensorPort")
public class SensorPort extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8862169824773793750L;

	private String name;
	
	private List<Sensor> sensorList=new ArrayList<Sensor>();
	
	private SensorInterface sensorInterface;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(cascade=CascadeType.MERGE,fetch=FetchType.LAZY,mappedBy="sensorPort",targetEntity=Sensor.class)
	public List<Sensor> getSensorList() {
		return sensorList;
	}

	public void setSensorList(List<Sensor> sensorList) {
		this.sensorList = sensorList;
	}

	@ManyToOne(fetch=FetchType.LAZY,targetEntity=SensorInterface.class)
	public SensorInterface getSensorInterface() {
		return sensorInterface;
	}

	public void setSensorInterface(SensorInterface sensorInterface) {
		this.sensorInterface = sensorInterface;
	}
	
}
