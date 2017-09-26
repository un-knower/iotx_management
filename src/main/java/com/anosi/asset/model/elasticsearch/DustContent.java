package com.anosi.asset.model.elasticsearch;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "iotx", type = "dustContent")
@Setting(settingPath = "elasticsearch-analyser.json")
public class DustContent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3794342819299164992L;
	
	@Id
	private Long id;

	@Field(type = FieldType.String, index = FieldIndex.analyzed, searchAnalyzer = "ik_max_word", analyzer = "ik_max_word", store = true)
	private String content;
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String companyName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}
