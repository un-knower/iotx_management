package com.anosi.asset.model.jpa;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.anosi.asset.model.elasticsearch.Content;
import com.anosi.asset.util.BeanRefUtil.ExtraName;

@Entity
@Table(name = "sensor")
/*
 * @NamedEntityGraphs({
 * 
 * @NamedEntityGraph( name = "sensor.all", attributeNodes = {
 * 
 * @NamedAttributeNode("sensorCategory"),
 * 
 * @NamedAttributeNode("sensorPort"),
 * 
 * @NamedAttributeNode(value = "sensorPort",subgraph = "sensorInterface") },
 * subgraphs = {
 * 
 * @NamedSubgraph(name = "sensorInterface", attributeNodes
 * = @NamedAttributeNode("sensorInterface")), }//延伸属性 ), })
 */
public class Sensor extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5359114601983561364L;

	private String name;// 命名规则,例如"tt3"

	private String parameterDescribe;// 描述,例如出水口温度

	@Content
	private String serialNo;

	private Dust dust;

	@Content(extractFields = { "sensorCategory.name" })
	private SensorCategory sensorCategory;

	private Long alarmQuantity;

	@Content
	private Double maxVal;

	private DataType dataType;

	@Content
	private Double minVal;

	private String unit;

	@ExtraName(name = "runStatus")
	private Boolean isWorked = false;

	@Column(unique = true, nullable = false)
	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Dust.class)
	public Dust getDust() {
		return dust;
	}

	public void setDust(Dust dust) {
		this.dust = dust;
	}

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = SensorCategory.class)
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

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Boolean getIsWorked() {
		return isWorked;
	}

	public void setIsWorked(Boolean isWorked) {
		this.isWorked = isWorked;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParameterDescribe() {
		return parameterDescribe;
	}

	public void setParameterDescribe(String parameterDescribe) {
		this.parameterDescribe = parameterDescribe;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	/***
	 * 获取plc地址
	 * 
	 * @return
	 */
	@Transient
	public String getPlc() {
		return serialNo.split("_")[1];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((serialNo == null) ? 0 : serialNo.hashCode())
				+ ((getId() == null) ? 0 : getId().hashCode());
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
		if (Objects.equals(serialNo, other.getSerialNo())) {
			return true;
		}
		if (Objects.equals(getId(), other.getId())) {
			return true;
		}
		return true;
	}

	public static enum DataType {
		INT, REAL;
	}

}
