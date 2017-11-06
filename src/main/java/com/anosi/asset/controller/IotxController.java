package com.anosi.asset.controller;

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
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.QIotx;
import com.anosi.asset.service.IotxService;
import com.anosi.asset.util.StringUtil;
import com.querydsl.core.types.Predicate;

@RestController
public class IotxController extends BaseController<Iotx> {

	private static final Logger logger = LoggerFactory.getLogger(IotxController.class);

	@Autowired
	private IotxService iotxService;

	/***
	 * 在所有关于iotx的请求之前，为查询条件中添加公司
	 * 
	 * @param companyId
	 * @param model
	 */
	@ModelAttribute
	public void interceptIotx(@QuerydslPredicate(root = Iotx.class) Predicate predicate,
			@RequestParam(value = "company.id", required = false) Long companyId, Model model) {
		Account account = sessionComponent.getCurrentUser();
		if (account != null) {
			if (!SessionComponent.isAdmin()) {
				model.addAttribute("predicate", QIotx.iotx.company.id.eq(account.getCompany().getId()).and(predicate));
			} else if (SessionComponent.isAdmin() && companyId != null) {
				model.addAttribute("predicate", QIotx.iotx.company.id.eq(companyId).and(predicate));
			} else {
				model.addAttribute("predicate", predicate);
			}
		}
	}
	
	/***
	 * 根据条件查询某个iox
	 * 
	 * @param showType
	 * @param predicate
	 * @param showAttributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/iotx/management/data/one", method = RequestMethod.GET)
	public JSONObject findIotxManageDataOne(@QuerydslPredicate(root = Iotx.class) Predicate predicate,
			@RequestParam(value = "showAttributes") String showAttributes) throws Exception {
		logger.info("find iotx one");
		return jsonUtil.parseAttributesToJson(StringUtil.splitAttributes(showAttributes),
				iotxService.findOne(predicate));
	}


	/***
	 * 进入iotx管理地图页面
	 * 
	 * @return
	 */
	@RequiresPermissions({ "iotxManagement:view" })
	@RequestMapping(value = "/iotx/management/map/view", method = RequestMethod.GET)
	public ModelAndView toViewIotxManageMap() {
		logger.info("view iotx management map");
		return new ModelAndView("iotx/managementMap");
	}

	/***
	 * 进入iotx管理表格页面
	 * 
	 * @return
	 */
	@RequiresPermissions({ "iotxManagement:view" })
	@RequestMapping(value = "/iotx/management/table/view", method = RequestMethod.GET)
	public ModelAndView toViewIotxManageTable() {
		logger.info("view iotx management table");
		return new ModelAndView("iotx/managementTable");
	}

	/***
	 * 获取iotx管理的数据
	 * 
	 * @param showType
	 * @param pageable
	 * @param predicate
	 *            querydsl自动绑定，形式:serialNo=abc&.....
	 * @param showAttributes
	 * @param rowId
	 * @param searchContent
	 *            模糊搜索的内容
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions({ "iotxManagement:view" })
	@RequestMapping(value = "/iotx/management/data/{showType}", method = RequestMethod.GET)
	public JSONObject findIotxManageData(@PathVariable ShowType showType,
			@PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
			@ModelAttribute Predicate predicate, @RequestParam(value = "showAttributes") String showAttributes,
			@RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId,
			@RequestParam(value = "searchContent", required = false) String searchContent) throws Exception {
		logger.info("find iotx");
		logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

		Page<Iotx> iotxs;
		if (StringUtils.isNoneBlank(searchContent)) {
			iotxs = iotxService.findByContentSearch(searchContent, pageable);
		} else {
			iotxs = iotxService.findAll(predicate, pageable);
		}

		return parseToJson(iotxs, rowId, showAttributes, showType);
	}

	/***
	 * 点击iotx详情进入的页面
	 * 
	 * @param iotxId
	 * @return
	 */
	@RequiresPermissions({ "iotxManagement:view", "dustManagement:view" })
	@RequestMapping(value = "/iotx/management/detail/{iotxId}/view", method = RequestMethod.GET)
	public ModelAndView toViewIotxManageTable(@PathVariable Long iotxId) throws Exception {
		logger.info("view iotx management detail");
		Iotx iotx = iotxService.getOne(iotxId);

		return new ModelAndView("iotx/managementDetail").addObject("iotx", iotx);
	}

	/**
	 * 按照Iotx某些属性判断是否存在
	 * 
	 * @param predicate
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/iotx/checkExist", method = RequestMethod.GET)
	public JSONObject checkExist(@ModelAttribute Predicate predicate) throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", iotxService.exists(predicate));
		return jsonObject;
	}

	/****
	 * 获取iotx分布的数据
	 * 
	 * @param predicate
	 * @param showAttributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/iotx/iotxDistribute/data", method = RequestMethod.GET)
	public JSONArray iotxDistribute(@ModelAttribute Predicate predicate) throws Exception {
		return iotxService.ascertainArea(predicate);
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
	@RequestMapping(value = "/iotx/autocomplete", method = RequestMethod.GET)
	public JSONArray autocomplete(@ModelAttribute Predicate predicate, @RequestParam(value = "label") String label,
			String value) throws Exception {
		return jsonUtil.parseAttributesToAutocomplete(label, value, iotxService.findAll(predicate));
	}

}
