package com.anosi.asset.service;

import java.io.InputStream;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.anosi.asset.model.jpa.Dust;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.Sensor;

public interface IotxRemoteService {

	/***
	 * 将values set到iotx
	 * 
	 * @param iotx
	 * @param values
	 * @return
	 * @throws Exception
	 */
	Iotx setValue(Iotx iotx, Map<String, Object> values) throws Exception;

	/***
	 * 将values set到dust
	 * 
	 * @param iotx
	 * @param values
	 * @return
	 * @throws Exception
	 */
	Dust setValue(Dust dust, Map<String, Object> values) throws Exception;

	Iotx save(Iotx iotx, InputStream is) throws Exception;
	
	Dust save(Dust dust, InputStream is) throws Exception;

	Sensor setValue(Sensor sensor, Map<String, Object> values) throws Exception;

	/***
	 * 上传配置文件
	 * 
	 * @param multipartFile
	 * @param identification
	 * @return
	 */
	String fileUpload(MultipartFile multipartFile, String identification);

}
