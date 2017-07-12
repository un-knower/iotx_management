package com.anosi.asset.model.jpa;

import java.util.ArrayList;
import java.util.Date;
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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Formula;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name="iotx")
public class Iotx extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2480716502123174880L;
	
	@NotBlank (message="{SNCannotBeNull}")
	private String serialNo;
	
	private String installLocation;
	
	private String cpu;
	
	private String memory;
	
	private String hardDisk;
	
	private String mac;
	
	private String version;
	
	private String url;
	
	private String wifiSsid;
	
	private Date openTime;
	
	private Long continueTime;
	
	private Long sensorQuantity;
	
	private Long alarmQuantity;
	
	private List<Sensor> sensorList = new ArrayList<Sensor>();
	
	@NotNull(message="{CompanyCannotBeNull}")
	private Company company;
	
	private NetworkCategory networkCategory;
	
	private IotxCategory iotxCategory;
	
	private IotxModel iotxModel;
	
	private Double longitude;//经度
	
	private Double latitude;//纬度
	
	private District district;//所属区县
	 
	//内部枚举类
	public static enum NetworkCategory{
		WIFI("wifi"),NET_2G("2G"),NET_3G("3G");
		
		private String network;

		private NetworkCategory(String network) {
			this.network = network;
		}

		public String getNetwork() {
			return network;
		}

		public void setNetwork(String network) {
			this.network = network;
		}
		
	}
	
	public static enum IotxCategory{
		IOTXCATEGORY("iotxCategory");
		
		private String category;

		private IotxCategory(String category) {
			this.category = category;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}
		
	}

	public static enum IotxModel{
		IOTXMODEL("iotxModel");
		
		private String model;

		private IotxModel(String model) {
			this.model = model;
		}

		public String getModel() {
			return model;
		}

		public void setModel(String model) {
			this.model = model;
		}
		
	}
	
	@Column(unique=true,nullable=false)
	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	@OneToMany(cascade=CascadeType.MERGE,fetch=FetchType.LAZY,mappedBy="iotx",targetEntity=Sensor.class)
	public List<Sensor> getSensorList() {
		return sensorList;
	}

	public void setSensorList(List<Sensor> sensorList) {
		this.sensorList = sensorList;
	}

	public String getInstallLocation() {
		return installLocation;
	}

	public void setInstallLocation(String installLocation) {
		this.installLocation = installLocation;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public String getMemory() {
		return memory;
	}

	public void setMemory(String memory) {
		this.memory = memory;
	}

	public String getHardDisk() {
		return hardDisk;
	}

	public void setHardDisk(String hardDisk) {
		this.hardDisk = hardDisk;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getWifiSsid() {
		return wifiSsid;
	}

	public void setWifiSsid(String wifiSsid) {
		this.wifiSsid = wifiSsid;
	}

	public Date getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}
	
	//获取关联的传感器数量
	@Formula("(select COUNT(*) from sensor s where s.iotx_id=id)")  
	public Long getSensorQuantity() {
		return sensorQuantity;
	}

	public void setSensorQuantity(Long sensorQuantity) {
		this.sensorQuantity = sensorQuantity;
	}

	public Long getAlarmQuantity() {
		return alarmQuantity;
	}

	public void setAlarmQuantity(Long alarmQuantity) {
		this.alarmQuantity = alarmQuantity;
	}

	@Transient
	public Long getContinueTime() {
		return continueTime;
	}

	public void setContinueTime(Long continueTime) {
		this.continueTime = continueTime;
	}
	@ManyToOne(fetch=FetchType.LAZY,targetEntity=Company.class)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public NetworkCategory getNetworkCategory() {
		return networkCategory;
	}

	public void setNetworkCategory(NetworkCategory networkCategory) {
		this.networkCategory = networkCategory;
	}

	public IotxCategory getIotxCategory() {
		return iotxCategory;
	}

	public void setIotxCategory(IotxCategory iotxCategory) {
		this.iotxCategory = iotxCategory;
	}

	public IotxModel getIotxModel() {
		return iotxModel;
	}

	public void setIotxModel(IotxModel iotxModel) {
		this.iotxModel = iotxModel;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	@ManyToOne(fetch=FetchType.LAZY,targetEntity=District.class)
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
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
		Iotx other = (Iotx) obj;
		if(Objects.equals(serialNo, other.getSerialNo())){
			return true;
		}
		if(Objects.equals(getId(), other.getId())){
			return true;
		}
		return true;
	}
	
}
