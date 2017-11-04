package com.anosi.asset.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.IndexedEmbedded;

@Entity
@Table(name = "city")
public class City extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2974444045399749731L;

	@Field
	private String name;
	
	@ContainedIn
	private List<District> districtList = new ArrayList<District>();
	
	@IndexedEmbedded
	private Province province;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@OneToMany(mappedBy="city", cascade={CascadeType.MERGE}, fetch = FetchType.LAZY)  
	public List<District> getDistrictList() {
		return districtList;
	}

	public void setDistrictList(List<District> districtList) {
		this.districtList = districtList;
	}
	@ManyToOne(fetch = FetchType.LAZY) 
	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
	}

}
