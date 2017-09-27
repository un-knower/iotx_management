package com.anosi.asset.dao.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.anosi.asset.model.elasticsearch.IotxDataContent;

public interface IotxDataContentDao extends BaseContentDao<IotxDataContent, String> {

	public Page<IotxDataContent> findByContentContainingAndAlarmStatusEquals(String content, boolean alarmStatus,
			Pageable pageable);

	public Page<IotxDataContent> findByContentContainingAndAlarmStatusAndCompanyNameEquals(String content,
			boolean alarmStatus, String companyName, Pageable pageable);

}
