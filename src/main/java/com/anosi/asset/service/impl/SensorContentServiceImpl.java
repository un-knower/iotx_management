package com.anosi.asset.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anosi.asset.dao.elasticsearch.BaseContentDao;
import com.anosi.asset.dao.elasticsearch.SensorContentDao;
import com.anosi.asset.model.elasticsearch.SensorContent;
import com.anosi.asset.model.jpa.Sensor;
import com.anosi.asset.service.SensorContentService;

@Service("sensorContentService")
@Transactional
public class SensorContentServiceImpl extends BaseContentServiceImpl<SensorContent, String, Sensor>
		implements SensorContentService {

	@Autowired
	private SensorContentDao sensorContentDao;

	@Override
	public BaseContentDao<SensorContent, String> getRepository() {
		return sensorContentDao;
	}

	@Override
	public SensorContent save(Sensor sensor) throws Exception {
		String id = String.valueOf(sensor.getId());
		SensorContent sensorContent = sensorContentDao.findOne(id);
		if (sensorContent == null) {
			sensorContent = new SensorContent();
			sensorContent.setId(id);
		}
		sensorContent.setCompanyName(sensor.getDust().getIotx().getCompany().getName());
		sensorContent = update(sensorContent, sensor);
		return sensorContent;
	}

}
