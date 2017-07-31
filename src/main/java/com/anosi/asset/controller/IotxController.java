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
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.Iotx.IotxCategory;
import com.anosi.asset.model.jpa.Iotx.IotxModel;
import com.anosi.asset.model.jpa.Iotx.NetworkCategory;
import com.anosi.asset.service.CompanyService;
import com.anosi.asset.service.IotxService;
import com.anosi.asset.util.JqgridUtil;
import com.anosi.asset.util.JsonUtil;
import com.querydsl.core.types.Predicate;

@RestController
public class IotxController extends BaseController<Iotx> {

	private static final Logger logger = LoggerFactory.getLogger(IotxController.class);

	@Autowired
	private IotxService iotxService;

	@Autowired
	private JqgridUtil<Iotx> jqgridUtil;
	
	@Autowired
	private JsonUtil<Iotx> jsonUtil;
	
	@Autowired
	private CompanyService companySerivce;

	/***
	 * 进入iotx管理地图页面
	 * @return
	 */
	@RequiresPermissions({"iotxManagement:view"})
	@RequestMapping(value = "/iotx/management/map", method = RequestMethod.GET)
	public ModelAndView toViewIotxManageMap() {
		logger.info("view iotx management map");
		return new ModelAndView("iotx/managementMap");
	}

	/***
	 * 进入iotx管理表格页面
	 * @return
	 */
	@RequiresPermissions({"iotxManagement:view"})
	@RequestMapping(value = "/iotx/management/table", method = RequestMethod.GET)
	public ModelAndView toViewIotxManageTable() {
		logger.info("view iotx management table");
		return new ModelAndView("iotx/managementTable").addObject("networkCategorys", NetworkCategory.values())
				.addObject("iotxCategorys", IotxCategory.values()).addObject("iotxModels", IotxModel.values())
				.addObject("companys", companySerivce.findAll());
	}

	/***
	 * 获取iotx管理的数据
	 * 
	 * @param pageable
	 * @param predicate
	 *            querydsl自动绑定，形式:serialNo=abc&.....
	 * @param showAttributes
	 * @param rowId
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions({"iotxManagement:view"})
	@RequestMapping(value = "/iotx/management/data", method = RequestMethod.GET)
	public JSONObject findIotxManageData(
			@PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable,
			@QuerydslPredicate(root = Iotx.class) Predicate predicate,
			@RequestParam(value = "showAttributes") String showAttributes, @RequestParam(value = "rowId") String rowId,
			@RequestParam(value = "company.code",required=false)String companyCode)
					throws Exception {
		logger.info("find iotx");
		logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		logger.debug("rowId:{},showAttributes:{}", rowId, showAttributes);
		
		this.checkCompany(companyCode);
		return jqgridUtil.parsePageToJqgridJson(iotxService.findAll(predicate, pageable), rowId,
				showAttributes.split(","));
	}

	/***
	 * 点击iotx详情进入的页面
	 * @param iotxId
	 * @return
	 */
	@RequiresPermissions({"iotxManagement:view","sensorManagement:view"})
	@RequestMapping(value = "/iotx/management/detail/{iotxId}", method = RequestMethod.GET)
	public ModelAndView toViewIotxManageTable(@PathVariable Long iotxId) throws Exception{
		logger.info("view iotx management detail");
		Iotx iotx = iotxService.findById(iotxId);
		this.checkCompany(iotx.getCompany().getCode());
		
		return new ModelAndView("iotx/managementDetail").addObject("iotx", iotx);
	}

	/***
	 * 进入save和update iotx的页面
	 * @param id
	 * @return
	 */
	@RequiresAuthentication
	@RequiresPermissions({"iotxManagement:add","iotxManagement:edit"})
	@RequestMapping(value = "/iotx/saveIotx", method = RequestMethod.GET)
	public ModelAndView toSaveIotxPage(@RequestParam(value = "id", required = false) Long id) throws Exception{
		Iotx iotx = null;
		if (id != null) {
			iotx = iotxService.findById(id);
			this.checkCompany(iotx.getCompany().getCode());
		} else {
			iotx = new Iotx();
		}
		return new ModelAndView("iotx/saveIotx").addObject("iotx", iotx).addObject("networkCategorys", NetworkCategory.values())
				.addObject("iotxCategorys", IotxCategory.values()).addObject("iotxModels", IotxModel.values())
				.addObject("companys", companySerivce.findAll());
	}

	@RequiresAuthentication
	@RequiresPermissions({"iotxManagement:add","iotxManagement:edit"})
	@RequestMapping(value = "/iotx/saveIotx", method = RequestMethod.POST)
	public JSONObject saveIotx(@Valid @ModelAttribute("iotx") Iotx iotx,BindingResult result) throws Exception {
		logger.debug("saveOrUpdate iotx");
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
			this.checkCompany(iotx.getCompany().getCode());
			iotxService.saveIotx(iotx);
			jsonObject.put("result", "success");
		}
		return jsonObject;
	}

	/****
	 * 在执行update前，先获取持久化的iotx对象
	 * 
	 * @param id
	 * @param model
	 * 
	 */
	@ModelAttribute
	public void getIox(@RequestParam(value = "iotxId", required = false) Long id, Model model) {
		if (id != null) {
			model.addAttribute("iotx", iotxService.findById(id));
		}
	}

	@RequiresAuthentication
	@RequiresPermissions({"iotxManagement:delete"})
	@RequestMapping(value = "/iotx/deleteIotx", method = RequestMethod.POST)
	public JSONObject deleteIotx(@RequestParam(value = "id")Long id) throws Exception {
		logger.debug("delete iotx");
		Iotx iotx = iotxService.findById(id);
		//检测一下companyCode
		this.checkCompany(iotx.getCompany().getCode());
		//检测无误就可以删除
		iotxService.deleteIotx(iotxService.findById(id));
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", "success");
		return jsonObject;
	}

	/**
	 * 按照Iotx某些属性判断是否存在
	 * @param predicate
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/iotx/checkExist", method = RequestMethod.GET)
	public JSONObject checkExist(@QuerydslPredicate(root = Iotx.class) Predicate predicate) throws Exception{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", iotxService.exists(predicate));
		return jsonObject;
	}
	
	/***
	 * 按照某些属性返回iotx的jsonarray
	 * @param predicate
	 * @param showAttributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/iotx/jsonArray", method = RequestMethod.GET)
	public JSONArray getIoxJsonArray(@QuerydslPredicate(root = Iotx.class) Predicate predicate,
			@RequestParam(value = "showAttributes") String showAttributes,@RequestParam(value = "company.code",required=false)String companyCode) throws Exception{
		this.checkCompany(companyCode);
		return jsonUtil.parseAttributesToJsonArray(showAttributes.split(","), iotxService.findAll(predicate));
	} 
	
	/****
	 * 获取iotx分布的数据
	 * @param predicate
	 * @param showAttributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/iotx/iotxDistribute", method = RequestMethod.GET)
	public JSONArray iotxDistribute(@QuerydslPredicate(root = Iotx.class) Predicate predicate,@RequestParam(value = "company.code",required=false)String companyCode) throws Exception{
		this.checkCompany(companyCode);
		return iotxService.ascertainArea(predicate);
	} 
	
	/***
	 * 获取autocomplete的source
	 * @param predicate
	 * @param label
	 * @param value
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/iotx/autocomplete", method = RequestMethod.GET)
	public JSONArray autocomplete(@QuerydslPredicate(root = Iotx.class) Predicate predicate,@RequestParam(value = "label") String label,
			String value,@RequestParam(value = "company.code",required=false)String companyCode) throws Exception{
		this.checkCompany(companyCode);
		return jsonUtil.parseAttributesToAutocomplete(label, value, iotxService.findAll(predicate));
	} 
	
	
}
