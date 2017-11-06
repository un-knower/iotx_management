package com.anosi.asset.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.component.I18nComponent;
import com.anosi.asset.component.RemoteComponent;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.jpa.Device;
import com.anosi.asset.model.jpa.Dust;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.Sensor;
import com.anosi.asset.service.CompanyService;
import com.anosi.asset.service.DeviceService;
import com.anosi.asset.service.DustService;
import com.anosi.asset.service.FileMetaDataService;
import com.anosi.asset.service.IotxRemoteService;
import com.anosi.asset.service.IotxService;
import com.anosi.asset.service.SensorService;
import com.anosi.asset.util.BeanRefUtil;
import com.anosi.asset.util.URLConncetUtil;
import com.google.common.collect.ImmutableMap;

@Service("iotxRemoteService")
@Transactional
public class IotxRemoteServiceImpl implements IotxRemoteService {

	@Autowired
	private IotxService iotxService;
	@Autowired
	private DustService dustService;
	@Autowired
	private SensorService sensorService;
	@Autowired
	private FileMetaDataService fileMetaDataService;
	@Autowired
	private DeviceService deviceService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private I18nComponent i18nComponent;
	@Autowired
	private RemoteComponent remoteComponent;

	/***
	 * 将上传的ini文件处理成map
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> convertStreamToMap(InputStream is, String section) throws Exception {
		Map<String, Object> map = new HashMap<>();
		Ini ini = new Ini(is);
		Section se = ini.get(section);
		for (Entry<String, String> entry : se.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
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
		Map<String, Object> map = new HashMap<>();
		Ini ini = new Ini(is);
		Section se = ini.get("system_conf");
		for (Entry<String, String> entry : se.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		String gps = map.get("gps").toString();
		map.put("longitude", gps.split(",")[0]);
		map.put("latitude", gps.split(",")[1]);
		iotx = iotxService.save(setValue(iotx, map));

		// 获取deviceSN,创建虚拟的dust进行关联
		Section section = ini.get("user_conf");
		String serialNo = section.get("device_no");
		Device device = deviceService.findBySerialNo(serialNo);
		if (device == null) {
			device = new Device();
			device.setCompany(companyService.findByName(i18nComponent.getMessage("goaland")));
			device.setSerialNo(section.get("device_no"));
			deviceService.save(device);
		}

		if (iotx.getDevice() == null) {
			Dust inventedDust = new Dust();
			inventedDust.setSerialNo(UUID.randomUUID().toString());
			inventedDust.setIotx(iotx);
			inventedDust.setDevice(device);
			dustService.save(inventedDust);
		} else {
			List<Dust> dustList = iotx.getDustList();
			for (Dust dust : dustList) {
				dust.setDevice(device);
			}
		}

		// 请求高澜项目,为设备设置坐标
		URLConncetUtil.sendPost(remoteComponent.getFullPath("/device/setDistrict"),
				"deviceSN=" + serialNo + "&longitude=" + iotx.getLongitude() + "&latitude=" + iotx.getLatitude());
		return iotx;
	}

	@Override
	public Dust save(Dust dust, InputStream is) throws Exception {
		return dustService.save(setValue(dust, convertStreamToMap(is, null)));
	}

	@Override
	public Sensor setValue(Sensor sensor, Map<String, Object> values) throws Exception {
		BeanRefUtil.setValue(sensor, values);
		return sensorService.save(sensor);
	}

	@Override
	public String fileUpload(MultipartFile multipartFile, String identification) {
		try {
			save(iotxService.findBySerialNo(identification) != null ? iotxService.findBySerialNo(identification)
					: new Iotx(), new ByteArrayInputStream(IOUtils.toByteArray(multipartFile.getInputStream())));// 流复用
			fileMetaDataService.saveFile(identification, multipartFile.getOriginalFilename(),
					multipartFile.getInputStream(), multipartFile.getSize());
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomRunTimeException("upload fail");
		}
		return new JSONObject(ImmutableMap.of("result", "success")).toString();
	}

}
