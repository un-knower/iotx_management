package com.anosi.asset.model.jpa;

import java.text.DecimalFormat;
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

import org.hibernate.annotations.Formula;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.springframework.util.CollectionUtils;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.anosi.asset.model.elasticsearch.Content;
import com.anosi.asset.util.BeanRefUtil.ExtraName;
import com.google.common.base.CharMatcher;

@Entity
@Table(name = "iotx")
@Indexed
@Analyzer(impl = IKAnalyzer.class)
public class Iotx extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2480716502123174880L;

	@Field(analyze = Analyze.NO)
	@Content
	// @JSONField(name="serial_no")
	@ExtraName(name = "unique_id")
	private String serialNo;

	private String cpu = "0mhz";

	private Double usedCpuPer = 0.0;

	private String memory = "0GB";

	private Double usedMemoryPer = 0.0;

	private String hardDisk = "0GB";

	private Double usedHardDiskPer = 0.0;

	private String mac;

	private String version;

	private Date openTime;

	private Long dustQuantity;

	private Long sensorQuantity;

	private Long alarmQuantity;

	private Long unConfirmAlarmQuantity;

	@ContainedIn
	private List<Dust> dustList = new ArrayList<>();

	@IndexedEmbedded
	@Content(extractFields = { "company.name" })
	private Company company;

	private NetworkCategory networkCategory;

	@Content(extractFields = { "status.status" })
	private Status status = Status.ONLINE;

	private Double longitude;// 经度

	private Double latitude;// 纬度

	// 为了在百度地图上显示点，还需要存储百度坐标
	private Double baiduLongitude;// 经度

	private Double baiduLatitude;// 纬度

	@IndexedEmbedded
	@Content(extractFields = { "district.name", "district.city.name", "district.city.province.name" })
	private District district;// 所属区县
	
	DecimalFormat df=new DecimalFormat(".##");

	// 内部枚举类
	public static enum NetworkCategory {
		WIFI("wifi"), NET_2G("2G"), NET_3G("3G");

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

	public static enum Status {
		ONLINE("在线"), OFFLINE("离线");

		private String status;

		private Status(String status) {
			this.status = status;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

	}

	@Column(unique = true, nullable = false)
	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	// 获取关联的微尘数量
	@Formula("(select COUNT(*) from dust d where d.iotx_id=id)")
	public Long getDustQuantity() {
		return dustQuantity;
	}

	public void setDustQuantity(Long dustQuantity) {
		this.dustQuantity = dustQuantity;
	}

	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "iotx", targetEntity = Dust.class)
	public List<Dust> getDustList() {
		return dustList;
	}

	public void setDustList(List<Dust> dustList) {
		this.dustList = dustList;
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

	/***
	 * 获取空闲内存的字符串形式
	 * 
	 * @return
	 */
	@Transient
	public String getFreeMemory() {
		double fullMemory = Double.parseDouble(CharMatcher.digit().retainFrom(memory));// 返回总量
		String unit = CharMatcher.javaLetter().retainFrom(memory);// 返回单位
		return df.format(fullMemory * (1 - usedMemoryPer)) + unit;
	}

	/***
	 * 获取使用内存的字符串形式
	 * 
	 * @return
	 */
	@Transient
	public String getUsedMemory() {
		double fullMemory = Double.parseDouble(CharMatcher.digit().retainFrom(memory));// 返回总量
		String unit = CharMatcher.javaLetter().retainFrom(memory);// 返回单位
		return df.format(fullMemory * usedMemoryPer) + unit;
	}

	public String getHardDisk() {
		return hardDisk;
	}

	public void setHardDisk(String hardDisk) {
		this.hardDisk = hardDisk;
	}

	/***
	 * 获取空闲硬盘的字符串形式
	 * 
	 * @return
	 */
	@Transient
	public String getFreeHardDisk() {
		double fullHardDisk = Double.parseDouble(CharMatcher.digit().retainFrom(hardDisk));// 返回总量
		String unit = CharMatcher.javaLetter().retainFrom(hardDisk);// 返回单位
		return df.format(fullHardDisk * (1 - usedHardDiskPer)) + unit;
	}

	/***
	 * 获取使用硬盘的字符串形式
	 * 
	 * @return
	 */
	@Transient
	public String getUsedHardDisk() {
		double fullHardDisk = Double.parseDouble(CharMatcher.digit().retainFrom(hardDisk));// 返回总量
		String unit = CharMatcher.javaLetter().retainFrom(hardDisk);// 返回单位
		return df.format(fullHardDisk * usedHardDiskPer) + unit;
	}

	public Double getUsedCpuPer() {
		return usedCpuPer;
	}

	public void setUsedCpuPer(Double usedCpuPer) {
		this.usedCpuPer = usedCpuPer;
	}

	public Double getUsedMemoryPer() {
		return usedMemoryPer;
	}

	public void setUsedMemoryPer(Double usedMemoryPer) {
		this.usedMemoryPer = usedMemoryPer;
	}

	public Double getUsedHardDiskPer() {
		return usedHardDiskPer;
	}

	public void setUsedHardDiskPer(Double usedHardDiskPer) {
		this.usedHardDiskPer = usedHardDiskPer;
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

	public Date getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}

	// 获取关联的传感器数量
	@Formula("(select COUNT(*) from sensor s left join dust d on s.dust_id = d.id where d.iotx_id=id)")
	public Long getSensorQuantity() {
		return sensorQuantity;
	}

	public void setSensorQuantity(Long sensorQuantity) {
		this.sensorQuantity = sensorQuantity;
	}

	@Formula("(select COUNT(*) from alarm_data a left join sensor s on a.sensor_id=s.id left join dust d on s.dust_id = d.id where d.iotx_id=id)")
	public Long getAlarmQuantity() {
		return alarmQuantity;
	}

	public void setAlarmQuantity(Long alarmQuantity) {
		this.alarmQuantity = alarmQuantity;
	}

	@Formula("(select COUNT(*) from alarm_data a left join sensor s on a.sensor_id=s.id left join dust d on s.dust_id = d.id where d.iotx_id=id and a.close_time is null)")
	public Long getUnConfirmAlarmQuantity() {
		return unConfirmAlarmQuantity;
	}

	public void setUnConfirmAlarmQuantity(Long unConfirmAlarmQuantity) {
		this.unConfirmAlarmQuantity = unConfirmAlarmQuantity;
	}

	@Transient
	public Long getContinueTime() {
		if (openTime != null) {
			return System.currentTimeMillis() - openTime.getTime();
		}
		return null;
	}

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Company.class)
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
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

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = District.class)
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public Double getBaiduLongitude() {
		return baiduLongitude;
	}

	public void setBaiduLongitude(Double baiduLongitude) {
		this.baiduLongitude = baiduLongitude;
	}

	public Double getBaiduLatitude() {
		return baiduLatitude;
	}

	public void setBaiduLatitude(Double baiduLatitude) {
		this.baiduLatitude = baiduLatitude;
	}

	/***
	 * 获取位置
	 * 
	 * @return (经度,纬度)
	 */
	@Transient
	public String getLocation() {
		return "(" + longitude + "," + latitude + ")";
	}

	/***
	 * 获取安装地点
	 * 
	 * @return
	 */
	@Transient
	public String getInstallLocation() {
		return district.getCity().getProvince().getCountry().getName() + "-"
				+ district.getCity().getProvince().getName() + "-" + district.getCity().getName() + "-"
				+ district.getName();
	}

	/***
	 * 获取所属设备
	 * 
	 * @return
	 */
	@Transient
	public Device getDevice() {
		if (CollectionUtils.isEmpty(dustList)) {
			return null;
		}
		return dustList.get(0).getDevice();
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
		Iotx other = (Iotx) obj;
		if (Objects.equals(serialNo, other.getSerialNo())) {
			return true;
		}
		if (Objects.equals(getId(), other.getId())) {
			return true;
		}
		return true;
	}

}
