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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.model.mongo.IotxData;
import com.anosi.asset.service.IotxDataService;
import com.anosi.asset.util.JqgridUtil;
import com.querydsl.core.types.Predicate;

@RestController
public class IotxDataController extends BaseController<IotxData>{
	
	private static final Logger logger = LoggerFactory.getLogger(IotxDataController.class);
	
	@Autowired
	private IotxDataService iotxDataService;
	
	@Autowired
	private JqgridUtil<IotxData> jqgridUtil;
	
	/**
	 * 进入传感器管理表格页面
	 * @return
	 */
	@RequiresPermissions({"iotxAlarmData:view"})
	@RequestMapping(value="/iotxData/management/table",method = RequestMethod.GET)
	public ModelAndView toViewsensorManage(){
		logger.info("view iotxData table");
		return new ModelAndView("iotxData/managementTable");
	}
	
	/***
	 * 获取传感器管理数据
	 * @param pageable
	 * @param predicate
	 * @param showAttributes
	 * @param rowId
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions({"iotxAlarmData:view"})
	@RequestMapping(value="/iotxData/management/data",method = RequestMethod.GET)
	public JSONObject findSensorManageData(@PageableDefault(sort={"id"}, direction = Sort.Direction.DESC,page=0,size=20) Pageable pageable,
			@QuerydslPredicate(root = IotxData.class) Predicate predicate,@RequestParam(value = "showAttributes")String showAttributes,
			@RequestParam(value = "rowId")String rowId,@RequestParam(value = "companyCode",required=false)String companyCode) throws Exception{
		logger.info("find sensor");
		logger.debug("page:{},size{},sort{}",pageable.getPageNumber(),pageable.getPageSize(),pageable.getSort());
		logger.debug("rowId:{},showAttributes:{}",rowId,showAttributes);
		
		this.checkCompany(companyCode);
		return jqgridUtil.parsePageToJqgridJson(iotxDataService.findAll(predicate, pageable), rowId, showAttributes.split(","));
	}
	
	@RequestMapping(value="/iotxData/dynamicData",method = RequestMethod.GET)
	public ModelAndView toViewDynamicData() throws Exception{
		return new ModelAndView("iotxData/dynamicData");
	}
	
	/***
	 * 获取动态线图上的数据
	 * @param sensorSN
	 * @param param
	 * @param timeUnit 时间单位
	 * @return
	 * @throws Exception 
	 */
	@RequiresPermissions({"iotxAlarmData:view"})
	@RequestMapping(value="/iotxData/dynamicData/{timeUnit}",method = RequestMethod.GET)
	public List<IotxData> findDynamicData(@QuerydslPredicate(root = IotxData.class) Predicate predicate,@PathVariable Integer timeUnit,
			@SortDefault(direction=Direction.DESC,value="collectTime")Sort sort,@RequestParam(value = "companyCode",required=false)String companyCode) throws Exception {
		logger.info("is going to findDynamicData");
		this.checkCompany(companyCode);
		return this.iotxDataService.findDynamicData(predicate, timeUnit,sort);
	}
	
}
