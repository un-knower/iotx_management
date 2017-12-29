package com.anosi.asset.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.anosi.asset.model.jpa.Sensor;
import com.anosi.asset.model.mongo.Message;

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
	 * @deprecated 在插入iotxdata时会设置这个值
	 */
	@Deprecated
	public Page<Sensor> setActualValue(Page<Sensor> sensors);

	/***
	 * 解析传感器元数据
	 * 
	 * @param payLoad
	 * @param values
	 */
	public Sensor convertSensor(Message payLoad);

	/***
	 * 解析传感器元数据
	 * 
	 * @param payLoad
	 * @param values
	 */
	public Sensor convertSensor(Message payLoad, Map<String, Object> values);
	
	public List<Sensor> findByDeviceSN(String deviceSN);
	
	public List<String> findSerialNoByDevice(String deviceSN);
	
}
