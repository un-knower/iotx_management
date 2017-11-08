package com.anosi.asset.model.jpa;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "iotxStatusPer")
public class IotxStatusPer extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 491548346889827300L;

	private Long online;
	
	private Long offline;
	
	private Company company;
	
	private Date countDate;

	public Long getOnline() {
		return online;
	}

	public void setOnline(Long online) {
		this.online = online;
	}

	public Long getOffline() {
		return offline;
	}

	public void setOffline(Long offline) {
		this.offline = offline;
	}

	public Date getCountDate() {
		return countDate;
	}

	public void setCountDate(Date countDate) {
		this.countDate = countDate;
	}

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Company.class)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
}
