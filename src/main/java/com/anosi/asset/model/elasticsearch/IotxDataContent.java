package com.anosi.asset.model.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "iotx", type = "iotxDataContent")
@Setting(settingPath = "elasticsearch-analyser.json")
public class IotxDataContent extends BaseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 427579536467104384L;

	@Field(type = FieldType.Boolean, store = true)
	private boolean alarmStatus;

	public boolean isAlarmStatus() {
		return alarmStatus;
	}

	public void setAlarmStatus(boolean alarmStatus) {
		this.alarmStatus = alarmStatus;
	}

}
