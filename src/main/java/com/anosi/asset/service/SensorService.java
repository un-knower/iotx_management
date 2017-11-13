package com.anosi.asset.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.anosi.asset.model.jpa.Sensor;

public interface SensorService extends BaseJPAService<Sensor> {

	public Sensor findBySerialNo(String serialNo);

	public Page<Sensor> findByContentSearch(String content, Pageable pageable, Long iotxId);

	/***
	 * 远程配置sensor
	 * 
	 * @param sensor
	 * @param isWorked
	 * @param frequency
	 * 
	 */
	public void remoteUpdate(Sensor sensor, boolean isWorked, Double frequency);

	/***
	 * 设置实时值
	 * 
	 * @param sensors
	 * @return
	 */
	public Page<Sensor> setActualValue(Page<Sensor> sensors);

}
