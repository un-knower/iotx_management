package com.anosi.asset.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.anosi.asset.model.elasticsearch.IotxDataContent;
import com.anosi.asset.model.mongo.IotxData;

public interface IotxDataContentService extends BaseContentService<IotxDataContent, String, IotxData>{

	public Page<IotxDataContent> findByContentAndAlarm(String content, boolean isAlarm,
			Pageable pageable);

	public Page<IotxDataContent> findByContentAndAlarmAndCompanyName(String content,
			boolean isAlarm, String companyName, Pageable pageable);
	
}
