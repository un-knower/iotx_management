package com.anosi.asset.controller;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

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
import com.anosi.asset.model.jpa.AlarmData;
import com.anosi.asset.model.jpa.QAlarmData;
import com.anosi.asset.service.AlarmDataService;
import com.anosi.asset.util.StringUtil;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathInits;

@RestController
public class AlarmDataController extends BaseController<AlarmData> {

	private static final Logger logger = LoggerFactory.getLogger(AlarmDataController.class);

	@Autowired
	private AlarmDataService alarmDataService;

	/***
	 * 在所有查询的请求之前，为查询条件中添加公司
	 * 
	 * @param companyId
	 * @param model
	 */
	@ModelAttribute
	public void interceptIotx(@QuerydslPredicate(root = AlarmData.class) Predicate predicate,
			@RequestParam(value = "company.id", required = false) Long companyId, Model model) {
		Account account = sessionComponent.getCurrentUser();
		if (account != null) {
			if (!SessionComponent.isAdmin()) {
				PathInits inits = new PathInits("sensor.dust.iotx.company");
				QAlarmData alarmData = new QAlarmData(AlarmData.class, forVariable("alarmData"), inits);
				model.addAttribute("predicate",
						alarmData.sensor.dust.iotx.company.id.eq(account.getCompany().getId()).and(predicate));
			} else if (SessionComponent.isAdmin() && companyId != null) {
				PathInits inits = new PathInits("sensor.dust.iotx.company");
				QAlarmData alarmData = new QAlarmData(AlarmData.class, forVariable("alarmData"), inits);
				model.addAttribute("predicate", alarmData.sensor.dust.iotx.company.id.eq(companyId).and(predicate));
			} else {
				model.addAttribute("predicate", predicate);
			}
		}
	}
	
	/***
	 * 根据条件查询某个alarmData
	 * 
	 * @param showType
	 * @param predicate
	 * @param showAttributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/alarmData/management/data/one", method = RequestMethod.GET)
	public JSONObject findAlarmDataManageDataOne(@QuerydslPredicate(root = AlarmData.class) Predicate predicate,
			@RequestParam(value = "showAttributes", required = false) String showAttributes) throws Exception {
		logger.info("find alarmData one");
		return jsonUtil.parseAttributesToJson(StringUtil.splitAttributes(showAttributes),
				alarmDataService.findOne(predicate));
	}

	/***
	 * 获取告警数据
	 * 
	 * @param showType
	 * @param pageable
	 * @param predicate
	 * @param showAttributes
	 * @param rowId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/alarmData/management/data/{showType}", method = RequestMethod.GET)
	public JSONObject findAlarmDataManageData(@PathVariable ShowType showType,
			@PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
			@ModelAttribute Predicate predicate, @RequestParam(value = "showAttributes", required = false) String showAttributes,
			@RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId) throws Exception {
		logger.info("find iotxData");
		logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);

		return parseToJson(alarmDataService.findAll(predicate, pageable), rowId, showAttributes, showType);
	}

}
