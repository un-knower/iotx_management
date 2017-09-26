package com.anosi.asset.controller;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.anosi.asset.service.IotxDataService;
import com.querydsl.core.types.Predicate;

@RestController
public class IotxDataController extends BaseController<IotxData> {

	private static final Logger logger = LoggerFactory.getLogger(IotxDataController.class);

	@Autowired
	private IotxDataService iotxDataService;

	/***
	 * 在所有关于dust的请求之前，为查询条件中添加公司
	 * 
	 * @param companyId
	 * @param model
	 */
	@ModelAttribute
	public void interceptDust(@QuerydslPredicate(root = IotxData.class) Predicate predicate,
			@RequestParam(value = "companyId", required = false) Long companyId, Model model) {
		Account account = SessionUtil.getCurrentUser();
		if (account != null) {
			if (!account.isAdmin()) {
				model.addAttribute("predicate",
						QIotxData.iotxData.companId.eq(account.getCompany().getId()).and(predicate));
			} else if (account.isAdmin() && companyId != null) {
				model.addAttribute("predicate", QIotxData.iotxData.companId.eq(companyId).and(predicate));
			}
		}
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
	 * 获取传感器告警数据
	 * 
	 * @param showType
	 * @param pageable
	 * @param predicate
	 * @param showAttributes
	 * @param rowId
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions({ "iotxAlarmData:view" })
	@RequestMapping(value = "/iotxData/management/data/{showType}", method = RequestMethod.GET)
	public JSONObject findIotxDataManageData(@PathVariable ShowType showType,
			@PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
			@ModelAttribute Predicate predicate, @RequestParam(value = "showAttributes") String showAttributes,
			@RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId) throws Exception {
		logger.info("find iotxData");
		logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

		return parseToJson(iotxDataService.findAll(predicate, pageable), rowId, showAttributes, showType);
	}

	@RequestMapping(value = "/iotxData/dynamicData", method = RequestMethod.GET)
	public ModelAndView toViewDynamicData() throws Exception {
		return new ModelAndView("iotxData/dynamicData");
	}

	/****
	 * 获取动态线图上的数据
	 * 
	 * @param predicate
	 * @param timeUnit
	 *            时间单位
	 * @param sort
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions({ "iotxAlarmData:view" })
	@RequestMapping(value = "/iotxData/dynamicData/{timeUnit}", method = RequestMethod.GET)
	public List<IotxData> findDynamicData(@ModelAttribute Predicate predicate, @PathVariable Integer timeUnit,
			@SortDefault(direction = Direction.DESC, value = "collectTime") Sort sort) throws Exception {
		logger.info("is going to findDynamicData");
		return this.iotxDataService.findDynamicData(predicate, timeUnit, sort);
	}

}
