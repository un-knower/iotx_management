package com.anosi.asset.controller;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.component.SessionComponent;
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
		Account account = sessionComponent.getCurrentUser();
		if (account != null) {
			if (!SessionComponent.isAdmin()) {
				PathInits inits = new PathInits("dust.iotx.company");
				QSensor sensor = new QSensor(Sensor.class, forVariable("sensor"), inits);
				model.addAttribute("predicate",
						sensor.dust.iotx.company.id.eq(account.getCompany().getId()).and(predicate));
			} else if (SessionComponent.isAdmin() && companyId != null) {
				PathInits inits = new PathInits("district.city.province");
				QSensor sensor = new QSensor(Sensor.class, forVariable("sensor"), inits);
				model.addAttribute("predicate", sensor.dust.iotx.company.id.eq(companyId).and(predicate));
			} else {
				model.addAttribute("predicate", predicate);
			}
		}
	}

	/***
	 * 进入sensor管理地图页面
	 * 
	 * @return
	 */
	@RequiresPermissions({ "sensorManagement:view" })
	@RequestMapping(value = "/sensor/management/map/view", method = RequestMethod.GET)
	public ModelAndView toViewSensorManageMap() {
		logger.info("view sensor management map");
		return new ModelAndView("sensor/managementMap");
	}

	/**
	 * 进入传感器管理表格页面
	 * 
	 * @return
	 */
	@RequiresPermissions({ "sensorManagement:view" })
	@RequestMapping(value = "/sensor/management/table/view", method = RequestMethod.GET)
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
	 * @param searchContent
	 *            模糊搜索的内容
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions({ "sensorManagement:view" })
	@RequestMapping(value = "/sensor/management/data/{showType}", method = RequestMethod.GET)
	public JSONObject findSensorManageData(@PathVariable ShowType showType,
			@PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
			@ModelAttribute Predicate predicate, @RequestParam(value = "showAttributes") String showAttributes,
			@RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId,
			@RequestParam(value = "searchContent", required = false) String searchContent) throws Exception {
		logger.info("find sensor");
		logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

		Page<Sensor> sensors;
		if (StringUtils.isNoneBlank(searchContent)) {
			sensors = sensorService.findByContentSearch(searchContent, pageable);
		} else {
			sensors = sensorService.findAll(predicate, pageable);
		}

		return parseToJson(sensors, rowId, showAttributes, showType);
	}

	/***
	 * 点进传感器查看详情的页面
	 * 
	 * @param sensorId
	 * @return
	 */
	@RequiresPermissions({ "sensorManagement:view", "iotxAlarmData:view" })
	@RequestMapping(value = "/sensor/management/detail/{sensorId}/view", method = RequestMethod.GET)
	public ModelAndView toViewSensorManageTable(@PathVariable Long sensorId) {
		logger.info("view sensor management detail");
		Sensor sensor = sensorService.getOne(sensorId);
		return new ModelAndView("sensor/managementDetail").addObject("sensor", sensor);
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
