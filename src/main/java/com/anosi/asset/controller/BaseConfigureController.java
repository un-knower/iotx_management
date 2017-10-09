package com.anosi.asset.controller;

import java.math.BigInteger;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.jpa.BaseEntity;
import com.anosi.asset.model.mongo.IotxData;
import com.anosi.asset.service.CompanyService;
import com.anosi.asset.service.DustService;
import com.anosi.asset.service.IotxDataService;
import com.anosi.asset.service.IotxService;
import com.anosi.asset.service.SensorService;
import com.google.common.collect.ImmutableMap;

/***
 * 关于iotx，dust，sensor的配置、iotxdata的修改都写在这个类里，
 * 这样原来的类里只有查询方法，适合用@ModelAttribute，防止其他参数绑定被影响
 * 
 * @author jinyao
 *
 */
@RestController
public class BaseConfigureController extends BaseController<BaseEntity> {

	private static final Logger logger = LoggerFactory.getLogger(BaseConfigureController.class);

	@Autowired
	private IotxService iotxService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DustService dustService;
	@Autowired
	private SensorService sensorService;
	@Autowired
	private IotxDataService iotxDataService;

	/***
	 * 进入远程update iotx的页面
	 * 
	 * @param id
	 * @return
	 */
	@RequiresAuthentication
	@RequiresPermissions({ "iotxManagement:edit" })
	@RequestMapping(value = "/iotx/update", method = RequestMethod.GET)
	public ModelAndView toUpdateIotxPage(@RequestParam(value = "id") Long id) throws Exception {
		return new ModelAndView("iotx/update").addObject("iotx", iotxService.getOne(id)).addObject("companys",
				companyService.findAll());
	}

	/***
	 * 配置iotx
	 * 
	 * @param id
	 * @param companyId
	 * @return
	 * @throws Exception
	 */
	@RequiresAuthentication
	@RequiresPermissions({ "iotxManagement:edit" })
	@RequestMapping(value = "/iotx/update", method = RequestMethod.POST)
	public JSONObject updateIotx(@RequestParam(value = "id") Long id,
			@RequestParam(value = "companyId", required = false) Long companyId) throws Exception {
		logger.debug("iotx configure");
		iotxService.remoteUpdate(iotxService.getOne(id), companyService.getOne(companyId));
		return new JSONObject(ImmutableMap.of("result", "success"));
	}

	/***
	 * 进入远程update dust的页面
	 * 
	 * @param id
	 * @return
	 */
	@RequiresAuthentication
	@RequiresPermissions({ "dustManagement:edit" })
	@RequestMapping(value = "/dust/update", method = RequestMethod.GET)
	public ModelAndView toUpdateDustPage(@RequestParam(value = "id") Long id) {
		return new ModelAndView("dust/update").addObject("dust", dustService.getOne(id));
	}

	/***
	 * 配置dust
	 * 
	 * @param id
	 * @param companyId
	 * @return
	 * @throws Exception
	 */
	@RequiresAuthentication
	@RequiresPermissions({ "dustManagement:edit" })
	@RequestMapping(value = "/dust/update", method = RequestMethod.POST)
	public JSONObject updateDust(@RequestParam(value = "id") Long id,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "frequency", required = false) Double frequency,
			@RequestParam(value = "isWorked", required = false) boolean isWorked) throws Exception {
		logger.debug("dust configure");
		dustService.remoteUpdate(dustService.getOne(id), name, frequency, isWorked);
		return new JSONObject(ImmutableMap.of("result", "success"));
	}

	/***
	 * 进入远程update sensor的页面
	 * 
	 * @param id
	 * @return
	 */
	@RequiresAuthentication
	@RequiresPermissions({ "sensorManagement:edit" })
	@RequestMapping(value = "/sensor/update", method = RequestMethod.GET)
	public ModelAndView toUpdateSensorPage(@RequestParam(value = "id") Long id) {
		return new ModelAndView("sensor/update").addObject("sensor", sensorService.getOne(id));
	}

	/***
	 * 配置sensor
	 * 
	 * @param id
	 * @param companyId
	 * @return
	 * @throws Exception
	 */
	@RequiresAuthentication
	@RequiresPermissions({ "sensorManagement:edit" })
	@RequestMapping(value = "/sensor/update", method = RequestMethod.POST)
	public JSONObject updateSensor(@RequestParam(value = "id") Long id,
			@RequestParam(value = "isWorked", required = false) boolean isWorked) throws Exception {
		logger.debug("sensor configure");
		sensorService.remoteUpdate(sensorService.getOne(id), isWorked);
		return new JSONObject(ImmutableMap.of("result", "success"));
	}

	/****
	 * 在执行update前，先获取持久化的iotxData对象
	 * 
	 * @param id
	 * @param model
	 * 
	 */
	@ModelAttribute
	public void getIotxData(@RequestParam(value = "iotxDataId", required = false) String stringId, Model model) {
		if (StringUtils.isNoneBlank(stringId)) {
			model.addAttribute("iotxData", iotxDataService.findOne(new BigInteger(stringId)));
		}
	}

	/***
	 * 修改iotxData
	 * 
	 * @param sensor
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/iotxData/save", method = RequestMethod.POST)
	public JSONObject save(@Valid @ModelAttribute("iotxData") IotxData iotxData, BindingResult result)
			throws Exception {
		logger.debug("saveOrUpdate iotxData");

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
			iotxDataService.save(iotxData);
			jsonObject.put("result", "success");
		}
		return jsonObject;
	}

}
