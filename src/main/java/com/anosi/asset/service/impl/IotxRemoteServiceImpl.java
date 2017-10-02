package com.anosi.asset.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.model.jpa.Dust;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.service.DustService;
import com.anosi.asset.service.IotxRemoteService;
import com.anosi.asset.service.IotxService;
import com.anosi.asset.util.BeanRefUtil;

@Service("iotxRemoteService")
@Transactional
public class IotxRemoteServiceImpl implements IotxRemoteService {

	@Autowired
	private IotxService iotxService;
	@Autowired
	private DustService dustService;

	/***
	 * 将上传的文件处理成map
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Object> convertStreamToMap(InputStream is) throws Exception {
		Properties pro = new Properties();
		pro.load(is);
		return new HashMap<String, Object>((Map) pro);
	}

	@Override
	public Iotx setValue(Iotx iotx, Map<String, Object> values) throws Exception {
		BeanRefUtil.setValue(iotx, values);
		return iotx;
	}

	@Override
	public Dust setValue(Dust dust, Map<String, Object> values) throws Exception {
		BeanRefUtil.setValue(dust, values);
		return dust;
	}

	@Override
	public Iotx save(Iotx iotx, InputStream is) throws Exception {
		return iotxService.save(setValue(iotx, convertStreamToMap(is)));
	}

	@Override
	public Dust save(Dust dust, InputStream is) throws Exception {
		return dustService.save(setValue(dust, convertStreamToMap(is)));
	}

	// TODO 批量添加微尘

}
