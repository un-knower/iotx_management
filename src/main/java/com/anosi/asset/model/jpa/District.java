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
@Table(name = "district")
public class District extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7555992276120785280L;
	
	@Field
	private String name;

	@IndexedEmbedded
	private City city;
	
	@ContainedIn
	private List<Iotx> iotxList = new ArrayList<Iotx>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@ManyToOne( fetch = FetchType.LAZY) 
	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}
	@OneToMany(mappedBy="district", cascade={CascadeType.MERGE}, fetch = FetchType.LAZY)  
	public List<Iotx> getIotxList() {
		return iotxList;
	}

	public void setIotxList(List<Iotx> iotxList) {
		this.iotxList = iotxList;
	}
	
}
