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
@Table(name = "province")
public class Province extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9184198336679250150L;

	@Field
	private String name;
	
	@ContainedIn
	private List<City> cityList = new ArrayList<City>();
	
	@IndexedEmbedded
	private Country country;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@OneToMany(mappedBy="province", cascade={CascadeType.MERGE}, fetch = FetchType.LAZY)  
	public List<City> getCityList() {
		return cityList;
	}

	public void setCityList(List<City> cityList) {
		this.cityList = cityList;
	}
	@ManyToOne(fetch = FetchType.LAZY) 
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}
	
}
