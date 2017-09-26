package com.anosi.asset.model.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "iotx", type = "sensorContent")
@Setting(settingPath = "elasticsearch-analyser.json")
public class SensorContent extends BaseContent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 685885304020037963L;

}
