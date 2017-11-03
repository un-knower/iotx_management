package com.anosi.asset.model.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "iotxgoaland", type = "dustContent")
@Setting(settingPath = "elasticsearch-analyser.json")
public class DustContent extends BaseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3794342819299164992L;

}
