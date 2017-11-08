package com.anosi.asset.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.component.SessionComponent;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.IotxStatusPer;
import com.anosi.asset.model.jpa.QIotxStatusPer;
import com.anosi.asset.service.IotxStatusPerService;
import com.querydsl.core.types.Predicate;

@RestController
public class IotxStatusPerController extends BaseController<IotxStatusPer>{
	
	private static final Logger logger = LoggerFactory.getLogger(IotxStatusPerController.class);
	
	@Autowired
	private IotxStatusPerService iotxStatusPerService;
	
	/***
	 * 在所有关于iotxStatusPer的请求之前，为查询条件中添加公司
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
				model.addAttribute("predicate", QIotxStatusPer.iotxStatusPer.company.id.eq(account.getCompany().getId()).and(predicate));
			} else if (SessionComponent.isAdmin() && companyId != null) {
				model.addAttribute("predicate", QIotxStatusPer.iotxStatusPer.company.id.eq(companyId).and(predicate));
			} else {
				model.addAttribute("predicate", predicate);
			}
		}
	}
	
	/***
	 * 获取iotxStatusPer管理的数据
	 * 
	 * @param showType
	 * @param pageable
	 * @param predicate
	 *            querydsl自动绑定，形式:serialNo=abc&.....
	 * @param showAttributes
	 * @param rowId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/iotxStatusPer/management/data/{showType}", method = RequestMethod.GET)
	public JSONObject findIotxManageData(@PathVariable ShowType showType,
			@PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
			@ModelAttribute Predicate predicate, @RequestParam(value = "showAttributes", required = false) String showAttributes,
			@RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId) throws Exception {
		logger.info("find iotx");
		logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

		return parseToJson(iotxStatusPerService.findAll(predicate, pageable), rowId, showAttributes, showType);
	}


}
