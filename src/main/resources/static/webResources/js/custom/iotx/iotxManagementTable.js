/**
 * 
 */
$(document).ready(function() {
		//不使用jqgrid默认的参数
		$.extend(jQuery.jgrid.defaults, {
		    prmNames: {
		        id: "_rowid", page: "_page", rows: "_rows",
		        oper: "_oper", sort: "_sidx", order: "_sord"
		    }
		});
		
		
		 var colModel=[
		               	{label:$.i18n.prop('iotx.serialNo'),name:'serialNo', index:'serialNo', width:'120', sortable: false,align: 'center'},
		               	{label:$.i18n.prop('iotx.company'),name:'company.name',index:'company.name', width: '120', sortable:false, align: 'center'},
						{label:$.i18n.prop('iotx.installLocation'),name:'installLocation',index:'installLocation', width: '120', sortable:false, align: 'center'},
						{label:$.i18n.prop('iotx.dustQuantity'),name:'dustQuantity', index:'dustQuantity',sortable: true, width:'120', align: 'center'},
						{label:$.i18n.prop('iotx.sensorQuantity'),name:'sensorQuantity', index:'sensorQuantity',sortable: true, width:'120', align: 'center'},
						{label:$.i18n.prop('iotx.alarmQuantity'),name:'alarmQuantity', index:'alarmQuantity',sortable: true, width:'120', align: 'center'},
						{label:$.i18n.prop('iotx.openTime'),name:'openTime', index:'openTime',sortable: true, width:'120', align: 'center'},
						{label:$.i18n.prop('iotx.status'),name:'status', index:'status',sortable: true, width:'120', align: 'center'},
						{
			                label:$.i18n.prop('operate'), name: 'operate', index: 'operate', width: 90, align:'center',
			                formatter: function (cellvalue, options, rowObject) {
			                	var hrefUrl='/iotx/management/detail/'+options.rowId+"/view";
			                    var detail="<input value='详情' type='button' onclick='window.location.href=\""+hrefUrl+"\"' class='btn btn-small btn-primary btn-xs' style='background:#434A5D;border-color:#0192D7;color:white;border-radius:15px 15px;width:80px'/>";
			                    return detail;
			                },
			            },
				   	  ];
		 
		 //每页显示多少行
		 var rowNum=20;
		 var page=0;
		 var url='/iotx/management/data/GRID';
		 var sort;
		 var selectRowId;
		 
		 //请求参数
		 var params={}
		 params['rowId']='id'
		 params['showAttributes']='serialNo,company.name,installLocation,dustQuantity,sensorQuantity,alarmQuantity,openTime,status.status';//要获取的属性名
		 params['page']=page;
		 params['size']=rowNum;
		 params['sort']=sort;
		 
		 var myGrid = jQuery("#iotxTable");
		 var myPager = jQuery("#iotxPager");
		 
		 myGrid.jqGrid({
	    		datatype: "json",
	    		url:url,
	    		postData:params,
	    		height: '100%',
	    	   	colModel:colModel,
	    	   	multiselect: false,
	    	   	multiboxonly: true,
	    	   	multiselectWidth: 30,
	    	   	rowNum: rowNum,
	    	   	autowidth: true,
	    	   	forceFit: false,
	    	   	altRows: false,
	    	   	viewrecords: true,
	    	   	
	    	   	gridComplete:function(){
	    	   	 	var lastPage = myGrid.getGridParam('lastpage');//获取总页数
	    	   		createPage(myGrid,myPager,lastPage,page,11,url,params);//调用自定义的方法来生成pager
		    	},
		    	
		    	//当触发排序时
		    	onSortCol:function(index,iCol,sortorder){
		    		params['sort']=index+","+sortorder;
		    		myGrid.jqGrid().setGridParam({
						url:url,
						postData:params,
					}).trigger("reloadGrid");
		    	}
		    	
	     });
		 
		 //edit按钮被点击
		 $("#edit").click(function(){
			 selectRowId = myGrid.getGridParam('selarrrow');
			 if(selectRowId!=null&&selectRowId!=""){
				 var func=function(){
					 if($("#iotxForm").valid()){
						 $("#iotxForm").submit();
						 return true;
					 }else{
						 return false;
					 }
				 };
				 createModalPage("配置iotx节点","/iotx/update?id="+selectRowId,func); 
			 }else{
				 warning("配置时必须选择一行");
			 }
		 })
		 
		 //查询按钮点击事件
		 $('#searchBtn').click(function(){
			var search = $('#toSearch').val();
			params['searchContent']=search;
			myGrid.jqGrid().setGridParam({
				url:url,
				postData:params,
			}).trigger("reloadGrid");
		 })
		 
		 //确定按钮点击事件
		 $('#confirm').click(function(){
			params['networkCategory'] = ($('#networkCategoryOption').val()==0)?null:$('#networkCategoryOption').val();
			params['company.id'] = ($('#companyOption').val()==0)?null:$('#companyOption').val();
			params['page']=0; 
			
			myGrid.jqGrid().setGridParam({
				url:url,
				postData:params,
			}).trigger("reloadGrid");
			
		});
	   
		//清空按钮点击事件
		$("#empty").click(function(){
			$("#networkCategoryOption").val(0);
			
			delete params['networkCategory'];
			delete params['company.id'];
		});
		
		//地图按钮点击事件
		$("#viewMap").click(function(){
			window.location.href='/iotx/management/map/view';
		});
		 
	});