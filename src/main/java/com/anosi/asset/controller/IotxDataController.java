package com.anosi.asset.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.component.SessionUtil;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.mongo.IotxData;
import com.anosi.asset.model.mongo.QIotxData;
import com.anosi.asset.service.CompanyService;
import com.anosi.asset.service.IotxDataService;
import com.anosi.asset.service.SensorService;
import com.querydsl.core.types.Predicate;

@RestController
public class IotxDataController extends BaseController<IotxData> {

	private static final Logger logger = LoggerFactory.getLogger(IotxDataController.class);

	@Autowired
	private IotxDataService iotxDataService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private SensorService sensorService;

	/***
	 * 在所有关于查询dust的请求之前，为查询条件中添加公司
	 * 
	 * @param companyId
	 * @param model
	 */
	@ModelAttribute
	public void interceptIotxData(@QuerydslPredicate(root = IotxData.class) Predicate predicate,
			@RequestParam(value = "companyId", required = false) Long companyId, Model model) {
		Account account = SessionUtil.getCurrentUser();
		if (account != null) {
			if (!SessionUtil.isAdmin()) {
				model.addAttribute("predicate",
						QIotxData.iotxData.companyName.eq(account.getCompany().getName()).and(predicate));
			} else if (SessionUtil.isAdmin() && companyId != null) {
				model.addAttribute("predicate",
						QIotxData.iotxData.companyName.eq(companyService.getOne(companyId).getName()).and(predicate));
			}
		}
	}

	/***
	 * 进入告警管理的地图页面
	 *
	 * @return
	 */
	@RequiresPermissions({ "iotxAlarmData:view" })
	@RequestMapping(value = "/iotxData/management/map", method = RequestMethod.GET)
	public ModelAndView toViewIotxDataManageMap() {
		logger.info("view iotxData management map");
		return new ModelAndView("iotxData/managementMap");
	}

	/**
	 * 进入传感器管理表格页面
	 * 
	 * @return
	 */
	@RequiresPermissions({ "iotxAlarmData:view" })
	@RequestMapping(value = "/iotxData/management/table", method = RequestMethod.GET)
	public ModelAndView toViewIotxDataManage() {
		logger.info("view iotxData table");
		return new ModelAndView("iotxData/managementTable");
	}

	/***
	 * 获取传感器数据
	 * 
	 * @param showType
	 * @param pageable
	 * @param predicate
	 * @param showAttributes
	 * @param rowId
	 * @param searchContent
	 *            是否有用到模糊搜索
	 * @param isAlarm
	 *            是否搜索告警数据
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions({ "iotxAlarmData:view" })
	@RequestMapping(value = "/iotxData/management/data/{showType}", method = RequestMethod.GET)
	public JSONObject findIotxDataManageData(@PathVariable ShowType showType,
			@PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
			@ModelAttribute Predicate predicate, @RequestParam(value = "showAttributes") String showAttributes,
			@RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId,
			@RequestParam(value = "searchContent", required = false) String searchContent,
			@RequestParam(value = "isAlarm", required = false) Boolean isAlarm) throws Exception {
		logger.info("find iotxData");
		logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

		Page<IotxData> iotxDatas;
		if (StringUtils.isNoneBlank(searchContent)) {
			if (isAlarm != null) {
				iotxDatas = iotxDataService.findByContentSearch(searchContent, isAlarm, predicate, pageable);
			} else {
				iotxDatas = iotxDataService.findByContentSearch(searchContent, predicate, pageable);
			}
		} else {
			iotxDatas = iotxDataService.findAll(predicate, pageable);
		}

		return parseToJson(iotxDatas, rowId, showAttributes, showType);
	}

	/***
	 * 获取线图数据
	 * 
	 * @param timeUnit
	 *            时间单位，用来区分月线，周线，日线等，以此来获取需要取出的数据数量
	 *            example:如果要看周线，就将一周时间换算成秒数:7*24*60*60,然后除以取样频率就获得了要获取的row总数
	 * @param sort
	 * @param predicate
	 * @param showAttributes
	 * @param dustSN
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions({ "iotxAlarmData:view" })
	@RequestMapping(value = "/iotxData/dynamicData", method = RequestMethod.GET)
	public JSONObject dynamicData(
			@RequestParam(value = "timeUnit", required = false, defaultValue = "360") Integer timeUnit,
			@SortDefault(value = "collectTime", direction = Direction.DESC) Sort sort,
			@ModelAttribute Predicate predicate, @RequestParam(value = "showAttributes") String showAttributes,
			@RequestParam(value = "sensorSN") String sensorSN) throws Exception {
		logger.info("find dynamicData");

		return parseToJson(
				iotxDataService.findDynamicData(predicate,
						sensorService.findBySerialNo(sensorSN).getDust().getFrequency(), timeUnit, sort),
				"id", showAttributes, ShowType.REMOTE);
	}

}
