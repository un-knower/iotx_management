package com.anosi.asset.model.jpa;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.Table;

@Entity
@Table(name="sensor")
@NamedEntityGraphs({
	@NamedEntityGraph(
				name = "sensor.all",
				attributeNodes = { 
						@NamedAttributeNode("sensorCategory"),
						@NamedAttributeNode("sensorPort"),
						@NamedAttributeNode(value = "sensorPort",subgraph = "sensorInterface")
				},
				subgraphs = {
						@NamedSubgraph(name = "sensorInterface", attributeNodes = @NamedAttributeNode("sensorInterface")),		  
				}//延伸属性
			),
	@NamedEntityGraph(
			name = "sensor.iotx.company",
			attributeNodes = { 
					@NamedAttributeNode("iotx"),
					@NamedAttributeNode(value = "iotx",subgraph = "company")
			},
			subgraphs = {
					@NamedSubgraph(name = "company", attributeNodes = @NamedAttributeNode("company")),		  
			}//延伸属性
		),
})
public class Sensor extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5359114601983561364L;
	
	private String serialNo;
	
	private Iotx iotx;
	
	private SensorPort sensorPort;
	
	private SensorCategory sensorCategory;
	
	private Long alarmQuantity;
	
	private Double maxVal;
	
	private Double minVal;
	
	@Column(unique=true,nullable=false)
	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	
	@ManyToOne(fetch=FetchType.LAZY,targetEntity=Iotx.class)
	public Iotx getIotx() {
		return iotx;
	}

	public void setIotx(Iotx iotx) {
		this.iotx = iotx;
	}
	
	@ManyToOne(fetch=FetchType.LAZY,targetEntity=SensorPort.class)
	public SensorPort getSensorPort() {
		return sensorPort;
	}

	public void setSensorPort(SensorPort sensorPort) {
		this.sensorPort = sensorPort;
	}

	@ManyToOne(fetch=FetchType.LAZY,targetEntity=SensorCategory.class)
	public SensorCategory getSensorCategory() {
		return sensorCategory;
	}

	public void setSensorCategory(SensorCategory sensorCategory) {
		this.sensorCategory = sensorCategory;
	}

	public Long getAlarmQuantity() {
		return alarmQuantity;
	}

	public void setAlarmQuantity(Long alarmQuantity) {
		this.alarmQuantity = alarmQuantity;
	}

	public Double getMaxVal() {
		return maxVal;
	}

	public void setMaxVal(Double maxVal) {
		this.maxVal = maxVal;
	}

	public Double getMinVal() {
		return minVal;
	}

	public void setMinVal(Double minVal) {
		this.minVal = minVal;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((serialNo == null) ? 0 : serialNo.hashCode())+((getId() == null) ? 0 : getId().hashCode());
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
		Sensor other = (Sensor) obj;
		if(Objects.equals(serialNo, other.getSerialNo())){
			return true;
		}
		if(Objects.equals(getId(), other.getId())){
			return true;
		}
		return true;
	}
	
}
