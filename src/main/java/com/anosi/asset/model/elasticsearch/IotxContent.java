package com.anosi.asset.model.elasticsearch;


import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "iotx", type = "iotxContent")
@Setting(settingPath = "elasticsearch-analyser.json")
public class IotxContent extends BaseContent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1224445206508094319L;

}
