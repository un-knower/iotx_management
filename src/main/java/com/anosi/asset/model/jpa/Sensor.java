package com.anosi.asset.model.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.anosi.asset.model.elasticsearch.Content;
import com.anosi.asset.util.BeanRefUtil.ExtraName;

@Entity
@Table(name = "sensor")
@Indexed
@Analyzer(impl = IKAnalyzer.class)
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

	@Field
	@ExtraName(name = "sensor_name")
	private String name;// 命名规则,例如"tt3"

	@Field
	@ExtraName(name = "description")
	private String parameterDescribe;// 描述,例如出水口温度

	@Field(analyze = Analyze.NO)
	@Content
	@ExtraName(name = "sensor_no")
	private String serialNo;

	@IndexedEmbedded
	private Dust dust;

	@Content(extractFields = { "sensorCategory.name" })
	private SensorCategory sensorCategory;

	private Long alarmQuantity;

	private Long unConfirmAlarmQuantity;

	@Content
	@ExtraName(name = "max_limit")
	private Double maxVal;

	@Content
	@ExtraName(name = "min_limit")
	private Double minVal;

	private String unit;

	@ExtraName(name = "runStatus")
	private DataType dataType;

	@ExtraName(name = "is_used")
	private Boolean isWorked = false;

	private List<AlarmData> alarmDataList = new ArrayList<>();

	private Double actualValue = 0.0;// 实时值

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

	@Formula("(select COUNT(*) from alarm_data a where a.sensor_id=id)")
	public Long getAlarmQuantity() {
		return alarmQuantity;
	}

	public void setAlarmQuantity(Long alarmQuantity) {
		this.alarmQuantity = alarmQuantity;
	}

	@Formula("(select COUNT(*) from alarm_data a where a.sensor_id=id and a.close_time is null)")
	public Long getUnConfirmAlarmQuantity() {
		return unConfirmAlarmQuantity;
	}

	public void setUnConfirmAlarmQuantity(Long unConfirmAlarmQuantity) {
		this.unConfirmAlarmQuantity = unConfirmAlarmQuantity;
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

	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "sensor", targetEntity = AlarmData.class)
	public List<AlarmData> getAlarmDataList() {
		return alarmDataList;
	}

	public void setAlarmDataList(List<AlarmData> alarmDataList) {
		this.alarmDataList = alarmDataList;
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

	public Double getActualValue() {
		return actualValue;
	}

	public void setActualValue(Double actualValue) {
		this.actualValue = actualValue;
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
