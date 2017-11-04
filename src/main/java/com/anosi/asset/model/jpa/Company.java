package com.anosi.asset.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;

@Entity
@Table(name = "company")
public class Company extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2865898828588906144L;

	@Field
	private String name;

	private String address;

	@ContainedIn
	private List<Iotx> iotxList = new ArrayList<>();

	private List<Account> accountList = new ArrayList<>();

	private List<Device> deviceList = new ArrayList<>();

	@Column(unique = true, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "company", targetEntity = Iotx.class)
	public List<Iotx> getIotxList() {
		return iotxList;
	}

	public void setIotxList(List<Iotx> iotxList) {
		this.iotxList = iotxList;
	}

	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "company", targetEntity = Account.class)
	public List<Account> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
	}

	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "company", targetEntity = Device.class)
	public List<Device> getDeviceList() {
		return deviceList;
	}

	public void setDeviceList(List<Device> deviceList) {
		this.deviceList = deviceList;
	}

}
