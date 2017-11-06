package com.anosi.asset.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.dao.jpa.AlarmDataDao;
import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.model.jpa.AlarmData;
import com.anosi.asset.service.AlarmDataService;

@Service("alarmDataService")
@Transactional
public class AlarmDataServiceImpl extends BaseJPAServiceImpl<AlarmData> implements AlarmDataService {

	@Autowired
	private AlarmDataDao alarmDataDao;

	@Override
	public BaseJPADao<AlarmData> getRepository() {
		return alarmDataDao;
	}

}
