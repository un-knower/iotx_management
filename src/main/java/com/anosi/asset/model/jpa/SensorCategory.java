package com.anosi.asset.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="sensorCategory")
public class SensorCategory extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9071498050250474896L;

	private String name;
	
	private List<Sensor> sensorList=new ArrayList<Sensor>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(cascade=CascadeType.MERGE,fetch=FetchType.LAZY,mappedBy="sensorCategory",targetEntity=Sensor.class)
	public List<Sensor> getSensorList() {
		return sensorList;
	}

	public void setSensorList(List<Sensor> sensorList) {
		this.sensorList = sensorList;
	}
	
}
