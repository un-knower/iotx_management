package com.anosi.asset.service;

import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONArray;
import com.anosi.asset.model.jpa.AlarmData;
import com.querydsl.core.types.Predicate;

public interface AlarmDataService extends BaseJPAService<AlarmData>{

	/***
	 * 将alarmData按照 天 来分组
	 * 
	 * @param pageable
	 * @param predicate
	 * @return
	 */
	JSONArray getAlarmDataDaily(Pageable pageable, Predicate predicate);

}
