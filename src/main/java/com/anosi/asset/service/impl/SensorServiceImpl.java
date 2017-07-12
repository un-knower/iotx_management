package com.anosi.asset.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.cache.annotation.SensorEvictCache;
import com.anosi.asset.cache.annotation.SensorSaveCache;
import com.anosi.asset.dao.jpa.SensorDao;
import com.anosi.asset.model.jpa.Sensor;
import com.anosi.asset.service.IotxDataService;
import com.anosi.asset.service.SensorService;
import com.querydsl.core.types.Predicate;


@Service("sensorService")
@Transactional
@CacheConfig(cacheNames="sensor")
public class SensorServiceImpl implements SensorService{
	
	private static final Logger logger = LoggerFactory.getLogger(SensorServiceImpl.class);
	
	@Autowired
	private SensorDao sensorDao;
	@Autowired
	private IotxDataService iotxDataService;

	@Override
	public Page<Sensor> findAll(Predicate predicate, Pageable pageable) {
		logger.debug("finAll by predicate:{}",predicate==null?"is null":predicate.toString());
		Page<Sensor> sensorPage = sensorDao.findAll(predicate, pageable);
		//为每个传感器设置告警数量
		for (Sensor sensor : sensorPage) {
			sensor.setAlarmQuantity(iotxDataService.countBysensorSN(sensor.getSerialNo()));
		}
		return sensorPage;
	}

	@Override
	public Sensor findById(Long sensorId) {
		Sensor sensor = sensorDao.findOne(sensorId);
		sensor.setAlarmQuantity(iotxDataService.countBysensorSN(sensor.getSerialNo()));
		return sensor;
	}

	@Override
	@SensorSaveCache  //缓存
	public Sensor saveSensor(Sensor sensor) {
		return sensorDao.save(sensor);
	}

	@Override
	@SensorEvictCache  //清理缓存
	public void deleteSensor(Sensor sensor) {
		sensorDao.delete(sensor);
	}

	@Override
	public boolean exists(Predicate predicate) {
		return sensorDao.exists(predicate);
	}

	@Override
	public Iterable<Sensor> findAll(Predicate predicate) {
		return sensorDao.findAll(predicate);
	}

	@Override
	@Cacheable(key="#serialNo+'ForSensor'")
	public Sensor findBySerialNo(String serialNo) {
		return sensorDao.findBySerialNoEquals(serialNo);
	}

}
