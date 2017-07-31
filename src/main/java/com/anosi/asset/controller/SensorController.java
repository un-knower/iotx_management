package com.anosi.asset.controller;

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
import com.anosi.asset.model.jpa.Sensor;
import com.anosi.asset.service.SensorCategoryService;
import com.anosi.asset.service.SensorInterfaceService;
import com.anosi.asset.service.SensorPortService;
import com.anosi.asset.service.SensorService;
import com.anosi.asset.util.JqgridUtil;
import com.anosi.asset.util.JsonUtil;
import com.querydsl.core.types.Predicate;

@RestController
public class SensorController extends BaseController<Sensor>{
	
	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	@Autowired
	private SensorService sensorService;
	@Autowired
	private SensorCategoryService sensorCategoryService;
	@Autowired
	private SensorInterfaceService sensorInterfaceService;
	@Autowired
	private SensorPortService sensorPortService;
	@Autowired
	private JqgridUtil<Sensor> jqgridUtil;
	@Autowired
	private JsonUtil<Sensor> jsonUtil;
	
	
	/**
	 * 进入传感器管理表格页面
	 * @return
	 */
	@RequiresPermissions({"sensorManagement:view"})
	@RequestMapping(value="/sensor/management/table",method = RequestMethod.GET)
	public ModelAndView toViewsensorManage(){
		logger.info("view sensorManage table");
		return new ModelAndView("sensor/managementTable").addObject("sensorCategorys", sensorCategoryService.findAll())
				.addObject("sensorInterfaces", sensorInterfaceService.findAll())
				.addObject("sensorPorts", sensorPortService.findAll());
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
	@RequiresPermissions({"sensorManagement:view"})
	@RequestMapping(value="/sensor/management/data",method = RequestMethod.GET)
	public JSONObject findSensorManageData(@PageableDefault(sort={"id"}, direction = Sort.Direction.DESC,page=0,size=20) Pageable pageable,
			@QuerydslPredicate(root = Sensor.class) Predicate predicate,@RequestParam(value = "showAttributes")String showAttributes,
			@RequestParam(value = "rowId")String rowId,@RequestParam(value = "iotx.company.code",required=false)String companyCode) throws Exception{
		logger.info("find sensor");
		logger.debug("page:{},size{},sort{}",pageable.getPageNumber(),pageable.getPageSize(),pageable.getSort());
		logger.debug("rowId:{},showAttributes:{}",rowId,showAttributes);
		
		this.checkCompany(companyCode);
		return jqgridUtil.parsePageToJqgridJson(sensorService.findAll(predicate, pageable), rowId, showAttributes.split(","));
	}
	
	/***
	 * 点进传感器查看详情的页面
	 * @param sensorId
	 * @return
	 */
	@RequiresPermissions({"sensorManagement:view","iotxAlarmData:view"})
	@RequestMapping(value="/sensor/management/detail/{sensorId}",method = RequestMethod.GET)
	public ModelAndView toViewSensorManageTable(@PathVariable Long sensorId){
		logger.info("view sensor management detail");
		Sensor sensor = sensorService.findById(sensorId);
		this.checkCompany(sensor.getIotx().getCompany().getCode());
		return new ModelAndView("sensor/managementDetail").addObject("sensor",sensor );
	}
	
	/***
	 * 进入save和update sensor的页面
	 * @param id
	 * @return
	 */
	@RequiresAuthentication
	@RequiresPermissions({"sensorManagement:add","sensorManagement:edit"})
	@RequestMapping(value = "/sensor/saveSensor", method = RequestMethod.GET)
	public ModelAndView toSaveSensorPage(@RequestParam(value = "id", required = false) Long id) {
		Sensor sensor = null;
		if (id != null) {
			sensor = sensorService.findById(id);
			this.checkCompany(sensor.getIotx().getCompany().getCode());
		} else {
			sensor = new Sensor();
		}
		return new ModelAndView("sensor/saveSensor").addObject("sensor", sensor).addObject("sensorCategorys", sensorCategoryService.findAll())
				.addObject("sensorInterfaces", sensorInterfaceService.findAll()).addObject("sensorPorts", sensorPortService.findAll());
	}

	@RequiresAuthentication
	@RequiresPermissions({"sensorManagement:add","sensorManagement:edit"})
	@RequestMapping(value = "/sensor/saveSensor", method = RequestMethod.POST)
	public JSONObject saveSensor(@Valid @ModelAttribute("sensor") Sensor sensor,BindingResult result) throws Exception {
		logger.debug("saveOrUpdate sensor");
		sensorService.saveSensor(sensor);
		JSONObject jsonObject = new JSONObject();
		//valid是否有错误
		if(result.hasErrors()){
			List<ObjectError>  list = result.getAllErrors();
			StringBuffer stringBuffer = new StringBuffer();
            for(ObjectError error:list){
            	stringBuffer.append(error.getCode()+"---"+error.getArguments()+"---"+error.getDefaultMessage()+"\n");
            }
            jsonObject.put("result", stringBuffer.toString());
		}else{
			//valid无误的时候,再检测一下companyCode
			this.checkCompany(sensor.getIotx().getCompany().getCode());
			sensorService.saveSensor(sensor);
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
			model.addAttribute("sensor", sensorService.findById(id));
		}
	}

	@RequiresAuthentication
	@RequiresPermissions({"sensorManagement:delete"})
	@RequestMapping(value = "/sensor/deleteSensor", method = RequestMethod.POST)
	public JSONObject deleteSensor(@RequestParam(value = "id") Long id) throws Exception {
		logger.debug("delete sensor");
		Sensor sensor = sensorService.findById(id);
		//检测一下companyCode
		this.checkCompany(sensor.getIotx().getCompany().getCode());
		//检测无误就可以删除
		sensorService.deleteSensor(sensorService.findById(id));
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", "success");
		return jsonObject;
	}

	/**
	 * 按照Sensor某些属性判断是否存在
	 * @param predicate
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/sensor/checkExist", method = RequestMethod.GET)
	public JSONObject checkExist(@QuerydslPredicate(root = Sensor.class) Predicate predicate,@RequestParam(value = "iotx.company.code",required=false)Long companyCode) throws Exception{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", sensorService.exists(predicate));
		return jsonObject;
	}
	
	/***
	 * 获取autocomplete的source
	 * @param predicate
	 * @param label
	 * @param value
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/sensor/autocomplete", method = RequestMethod.GET)
	public JSONArray autocomplete(@QuerydslPredicate(root = Sensor.class) Predicate predicate,@RequestParam(value = "label") String label,
			String value,@RequestParam(value = "iotx.company.code",required=false)String companyCode) throws Exception{
		return jsonUtil.parseAttributesToAutocomplete(label, value, sensorService.findAll(predicate));
	} 
	
}
