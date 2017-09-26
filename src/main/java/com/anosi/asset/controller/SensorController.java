package com.anosi.asset.controller;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import java.util.List;

import javax.validation.Valid;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
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
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.component.SessionUtil;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.QSensor;
import com.anosi.asset.model.jpa.Sensor;
import com.anosi.asset.service.SensorCategoryService;
import com.anosi.asset.service.SensorService;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathInits;

@RestController
public class SensorController extends BaseController<Sensor> {

	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

	@Autowired
	private SensorService sensorService;
	@Autowired
	private SensorCategoryService sensorCategoryService;

	/***
	 * 在所有关于sensor的请求之前，为查询条件中添加公司
	 * 
	 * @param companyId
	 * @param model
	 */
	@ModelAttribute
	public void interceptSensor(@QuerydslPredicate(root = Sensor.class) Predicate predicate,
			@RequestParam(value = "dust.iotx.company.id", required = false) Long companyId, Model model) {
		Account account = SessionUtil.getCurrentUser();
		if (account != null) {
			if (!account.isAdmin()) {
				PathInits inits = new PathInits("district.city.province");
				QSensor sensor = new QSensor(Sensor.class, forVariable("sensor"), inits);
				model.addAttribute("predicate",
						sensor.dust.iotx.company.id.eq(account.getCompany().getId()).and(predicate));
			} else if (account.isAdmin() && companyId != null) {
				PathInits inits = new PathInits("district.city.province");
				QSensor sensor = new QSensor(Sensor.class, forVariable("sensor"), inits);
				model.addAttribute("predicate", sensor.dust.iotx.company.id.eq(companyId).and(predicate));
			}
		}
	}

	/**
	 * 进入传感器管理表格页面
	 * 
	 * @return
	 */
	@RequiresPermissions({ "sensorManagement:view" })
	@RequestMapping(value = "/sensor/management/table", method = RequestMethod.GET)
	public ModelAndView toViewsensorManage() {
		logger.info("view sensorManage table");
		return new ModelAndView("sensor/managementTable").addObject("sensorCategorys", sensorCategoryService.findAll());
	}

	/***
	 * 获取传感器管理数据
	 * 
	 * @param showType
	 * @param pageable
	 * @param predicate
	 * @param showAttributes
	 * @param rowId
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions({ "sensorManagement:view" })
	@RequestMapping(value = "/sensor/management/data/{showType}", method = RequestMethod.GET)
	public JSONObject findSensorManageData(@PathVariable ShowType showType,
			@PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
			@ModelAttribute Predicate predicate, @RequestParam(value = "showAttributes") String showAttributes,
			@RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId) throws Exception {
		logger.info("find sensor");
		logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

		return parseToJson(sensorService.findAll(predicate, pageable), rowId, showAttributes, showType);
	}

	/***
	 * 点进传感器查看详情的页面
	 * 
	 * @param sensorId
	 * @return
	 */
	@RequiresPermissions({ "sensorManagement:view", "iotxAlarmData:view" })
	@RequestMapping(value = "/sensor/management/detail/{sensorId}", method = RequestMethod.GET)
	public ModelAndView toViewSensorManageTable(@PathVariable Long sensorId) {
		logger.info("view sensor management detail");
		Sensor sensor = sensorService.getOne(sensorId);
		return new ModelAndView("sensor/managementDetail").addObject("sensor", sensor);
	}

	/***
	 * 进入save和update sensor的页面
	 * 
	 * @param id
	 * @return
	 */
	@RequiresAuthentication
	@RequiresPermissions({ "sensorManagement:add", "sensorManagement:edit" })
	@RequestMapping(value = "/sensor/save", method = RequestMethod.GET)
	public ModelAndView toSaveSensorPage(@RequestParam(value = "id", required = false) Long id) {
		Sensor sensor = null;
		if (id != null) {
			sensor = sensorService.getOne(id);
		} else {
			sensor = new Sensor();
		}
		return new ModelAndView("sensor/save").addObject("sensor", sensor).addObject("sensorCategorys",
				sensorCategoryService.findAll());
	}

	@RequiresAuthentication
	@RequiresPermissions({ "sensorManagement:add", "sensorManagement:edit" })
	@RequestMapping(value = "/sensor/save", method = RequestMethod.POST)
	public JSONObject saveSensor(@Valid @ModelAttribute("sensor") Sensor sensor, BindingResult result)
			throws Exception {
		logger.debug("saveOrUpdate sensor");
		sensorService.save(sensor);
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
	public void getIox(@RequestParam(value = "sensorId", required = false) Long id, Model model) {
		if (id != null) {
			model.addAttribute("sensor", sensorService.getOne(id));
		}
	}

	@RequiresAuthentication
	@RequiresPermissions({ "sensorManagement:delete" })
	@RequestMapping(value = "/sensor/delete", method = RequestMethod.POST)
	public JSONObject deleteSensor(@RequestParam(value = "id") Long id) throws Exception {
		logger.debug("delete sensor");
		sensorService.delete(id);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", "success");
		return jsonObject;
	}

	/**
	 * 按照Sensor某些属性判断是否存在
	 * 
	 * @param predicate
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/sensor/checkExist", method = RequestMethod.GET)
	public JSONObject checkExist(@ModelAttribute Predicate predicate) throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", sensorService.exists(predicate));
		return jsonObject;
	}

	/***
	 * 获取autocomplete的source
	 * 
	 * @param predicate
	 * @param label
	 * @param value
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/sensor/autocomplete", method = RequestMethod.GET)
	public JSONArray autocomplete(@ModelAttribute Predicate predicate, @RequestParam(value = "label") String label,
			String value) throws Exception {
		return jsonUtil.parseAttributesToAutocomplete(label, value, sensorService.findAll(predicate));
	}

}
