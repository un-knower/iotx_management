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

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.component.SessionComponent;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.Dust;
import com.anosi.asset.model.jpa.QDust;
import com.anosi.asset.service.DustService;
import com.querydsl.core.types.Predicate;

@RestController
public class DustController extends BaseController<Dust>{

	private static final Logger logger = LoggerFactory.getLogger(DustController.class);

	@Autowired
	private DustService dustService;

	/***
	 * 在所有关于dust的请求之前，为查询条件中添加公司
	 * 
	 * @param companyId
	 * @param model
	 */
	@ModelAttribute
	public void interceptDust(@QuerydslPredicate(root = Dust.class) Predicate predicate,
			@RequestParam(value = "iotx.company.id", required = false) Long companyId, Model model) {
		Account account = sessionComponent.getCurrentUser();
		if (account != null) {
			if (!SessionComponent.isAdmin()) {
				model.addAttribute("predicate", QDust.dust.iotx.company.id.eq(account.getCompany().getId()).and(predicate));
			} else if (SessionComponent.isAdmin() && companyId != null) {
				model.addAttribute("predicate", QDust.dust.iotx.company.id.eq(companyId).and(predicate));
			} else {
				model.addAttribute("predicate", predicate);
			}
		}
	}
	
	/***
	 * 进入dust管理地图页面
	 * 
	 * @return
	 */
	@RequiresPermissions({ "dustManagement:view" })
	@RequestMapping(value = "/dust/management/map/view", method = RequestMethod.GET)
	public ModelAndView toViewDustManageMap() {
		logger.info("view dust management map");
		return new ModelAndView("dust/managementMap");
	}

	/***
	 * 进入dust管理表格页面
	 * 
	 * @return
	 */
	@RequiresPermissions({ "dustManagement:view" })
	@RequestMapping(value = "/dust/management/table/view", method = RequestMethod.GET)
	public ModelAndView toViewDustManageTable() {
		logger.info("view dust management table");
		return new ModelAndView("dust/managementTable");
	}

	/***
	 * 获取dust管理的数据
	 * 
	 * @param showType
	 * @param pageable
	 * @param predicate
	 * @param showAttributes
	 * @param rowId
	 * @param searchContent 模糊搜索的内容
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions({ "dustManagement:view" })
	@RequestMapping(value = "/dust/management/data/{showType}", method = RequestMethod.GET)
	public JSONObject findDustManageData(@PathVariable ShowType showType,
			@PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
			@ModelAttribute Predicate predicate, @RequestParam(value = "showAttributes") String showAttributes,
			@RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId,
			@RequestParam(value = "searchContent", required = false) String searchContent) throws Exception {
		logger.info("find dust");
		logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

		Page<Dust> dusts;
		if(StringUtils.isNoneBlank(searchContent)){
			dusts = dustService.findByContentSearch(searchContent, pageable);
		}else{
			dusts = dustService.findAll(predicate, pageable);
		}
		
		return parseToJson(dusts, rowId, showAttributes, showType);
	}
	
	/***
	 * 点击dust详情进入的页面
	 * 
	 * @param dustId
	 * @return
	 */
	@RequiresPermissions({ "dustManagement:view", "sensorManagement:view" })
	@RequestMapping(value = "/dust/management/detail/{dustId}/view", method = RequestMethod.GET)
	public ModelAndView toViewDustManageTable(@PathVariable Long dustId) throws Exception {
		logger.info("view dust management detail");
		return new ModelAndView("dust/managementDetail").addObject("dust", dustService.getOne(dustId));
	}
	
	/**
	 * 按照Iotx某些属性判断是否存在
	 * 
	 * @param predicate
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dust/checkExist", method = RequestMethod.GET)
	public JSONObject checkExist(@ModelAttribute Predicate predicate) throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", dustService.exists(predicate));
		return jsonObject;
	}
	
}
