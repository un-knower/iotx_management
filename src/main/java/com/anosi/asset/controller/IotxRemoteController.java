package com.anosi.asset.controller;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.jpa.Dust;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.QIotx;
import com.anosi.asset.model.jpa.Sensor;
import com.anosi.asset.model.mongo.FileMetaData;
import com.anosi.asset.service.DustService;
import com.anosi.asset.service.IotxRemoteService;
import com.anosi.asset.service.IotxService;
import com.anosi.asset.service.SensorService;
import com.google.common.collect.ImmutableMap;

/****
 * 专门用来与处理由iotx发起的请求,包括如下操作:
 * 
 * <pre>
 * iotx和微尘<b>配置文件</b>的上传下载.
 * iotx、微尘、传感器<b>字段信息</b>的上传更新.
 * </pre>
 * 
 * @author jinyao
 *
 */
@RestController
@RequestMapping("/iotxRemote")
public class IotxRemoteController extends BaseController<Iotx> {

	private static final Logger logger = LoggerFactory.getLogger(IotxRemoteController.class);

	@Autowired
	private IotxService iotxService;
	@Autowired
	private DustService dustService;
	@Autowired
	private SensorService sensorService;
	@Autowired
	private IotxRemoteService iotxRemoteService;
	@Autowired
	private FileUpDownLoadController fileUpDownLoadController;

	/***
	 * 检查签名,签名不通过会抛出异常
	 * 
	 * <p>
	 * 签名格式:iotxSN_anosi_hehe_timestamp
	 * </p>
	 * 
	 * @param sign
	 */
	@ModelAttribute
	public void checkSign(@RequestParam(value = "sign") String sign) {
		String[] signs = sign.split("_");
		try {
			if (iotxService.exists(QIotx.iotx.serialNo.eq(signs[0]))) {
				throw new CustomRunTimeException("sign illegal");
			}
			if (Objects.equals(signs[1], "anosi")) {
				throw new CustomRunTimeException("sign illegal");
			}
			if (Objects.equals(signs[2], "hehe")) {
				throw new CustomRunTimeException("sign illegal");
			}
			if (Long.parseLong(signs[3]) < System.currentTimeMillis()) {
				throw new CustomRunTimeException("sign illegal");
			}
		} catch (Exception e) {
			throw new CustomRunTimeException("sign illegal");
		}
	}

	/***
	 * 添加iotx
	 * 
	 * @param iotx
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/iotx/save", method = RequestMethod.POST)
	public JSONObject saveIotx(@Valid @ModelAttribute("iotx") Iotx iotx, BindingResult result) throws Exception {
		logger.debug("saveOrUpdate iotx");
		JSONObject jsonObject = new JSONObject();
		// valid是否有错误
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			StringBuffer stringBuffer = new StringBuffer();
			for (ObjectError error : list) {
				stringBuffer.append(
						error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage() + "\n");
			}
			jsonObject.put("result", stringBuffer.toString());
		} else {
			iotxService.save(iotx);
			jsonObject.put("result", "success");
		}
		return jsonObject;
	}

	/****
	 * 在执行update前，先获取持久化的iotx对象
	 * 
	 * @param id
	 * @param model
	 * 
	 */
	@ModelAttribute
	public void getIox(@RequestParam(value = "iotxSN", required = false) String iotxSN, Model model) {
		if (StringUtils.isNoneBlank(iotxSN)) {
			model.addAttribute("iotx", iotxService.findBySerialNo(iotxSN));
		}
	}

	/****
	 * 添加dust
	 * 
	 * @param dust
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dust/save", method = RequestMethod.POST)
	public JSONObject saveDust(@Valid @ModelAttribute("dust") Dust dust, BindingResult result) throws Exception {
		logger.debug("saveOrUpdate dust");
		JSONObject jsonObject = new JSONObject();
		// valid是否有错误
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			StringBuffer stringBuffer = new StringBuffer();
			for (ObjectError error : list) {
				stringBuffer.append(
						error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage() + "\n");
			}
			jsonObject.put("result", stringBuffer.toString());
		} else {
			dustService.save(dust);
			jsonObject.put("result", "success");
		}
		return jsonObject;
	}

	/****
	 * 在执行update前，先获取持久化的dust对象
	 * 
	 * @param id
	 * @param model
	 * 
	 */
	@ModelAttribute
	public void getDust(@RequestParam(value = "dustSN", required = false) String dustSN, Model model) {
		if (StringUtils.isNoneBlank(dustSN)) {
			model.addAttribute("dust", dustService.findBySerialNo(dustSN));
		}
	}

	/***
	 * 添加sensor
	 * 
	 * @param sensor
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/sensor/save", method = RequestMethod.POST)
	public JSONObject saveSensor(@Valid @ModelAttribute("sensor") Sensor sensor, BindingResult result)
			throws Exception {
		logger.debug("saveOrUpdate sensor");
		
		JSONObject jsonObject = new JSONObject();
		// valid是否有错误
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			StringBuffer stringBuffer = new StringBuffer();
			for (ObjectError error : list) {
				stringBuffer.append(
						error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage() + "\n");
			}
			jsonObject.put("result", stringBuffer.toString());
		} else {
			sensorService.save(sensor);
			jsonObject.put("result", "success");
		}
		return jsonObject;
	}

	/****
	 * 在执行update前，先获取持久化的sensor对象
	 * 
	 * @param id
	 * @param model
	 * 
	 */
	@ModelAttribute
	public void getSensor(@RequestParam(value = "sensorSN", required = false) String sensorSN, Model model) {
		if (StringUtils.isNoneBlank(sensorSN)) {
			model.addAttribute("sensor", sensorService.findBySerialNo(sensorSN));
		}
	}

	/***
	 * 上传文件,目前只会上传iotx配置文件,并且不会批量上传
	 * 
	 * @param multipartFile
	 * @param identification
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/fileUpload/multipartFiles/{identification}", method = RequestMethod.POST)
	public String fileUpload(@RequestParam("file_upload") MultipartFile multipartFile,
			@PathVariable String identification) throws Exception {
		if(multipartFile==null){
			return new JSONObject(ImmutableMap.of("result", "file is null")).toString();
		}
		return iotxRemoteService.fileUpload(multipartFile, identification);
	}

	/***
	 * 根据文件组来获取可下载文件的列表
	 * 
	 * @param identification
	 * @throws Exception
	 */
	@RequestMapping(value = "/fileDownload/list/group/{identification}", method = RequestMethod.GET)
	public Page<FileMetaData> fileDownloadListGroup(@PathVariable String identification,
			@PageableDefault(sort = {
					"uploadTime" }, direction = Sort.Direction.DESC, page = 0, value = 20) Pageable pageable)
			throws Exception {
		return fileUpDownLoadController.fileDownloadListGroup(identification, pageable);
	}

	/****
	 * 下载文件
	 * 
	 * @param identification
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/fileDownload/{objectId}", method = RequestMethod.GET)
	public String fileDownload(@PathVariable BigInteger objectId, HttpServletResponse response) throws Exception {
		return fileUpDownLoadController.fileDownload(objectId, response);
	}

}
