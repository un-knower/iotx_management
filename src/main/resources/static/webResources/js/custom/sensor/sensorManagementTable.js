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
		               	{label:'序列号',name:'serialNo', index:'serialNo', width:'120', sortable: false,align: 'center'},
						{label:'所属微尘',name:'dust.serialNo',index:'dust.serialNo', width: '120', sortable:false, align: 'center'},
						{label:'告警数量',name:'alarmQuantity', index:'alarmQuantity', width:'120', sortable: false,align: 'center'},
						{label:'采集频率',name:'dust.frequency', index:'dust.frequency', width:'120', sortable: false,align: 'center'},
						{label:'最大值',name:'maxVal', index:'maxVal', width:'120', sortable: false,align: 'center'},
						{label:'最小值',name:'minVal', index:'minVal', width:'120', sortable: false,align: 'center'},
						{label:'是否采集',name:'isWorked', index:'isWorked', width:'120', sortable: false,align: 'center'},
						{label:'连接设备序列号',name:'dust.device.serialNo', index:'dust.device.serialNo', width:'120', sortable: false,align: 'center'},
						{
		                    label:'操作', name: 'operate', index: 'operate', width: 50, align:'center',
		                    formatter: function (cellvalue, options, rowObject) {
		                    	var hrefUrl='/sensor/management/detail/'+options.rowId+"/view";
		                        var detail="<input value='详情' type='button' onclick='window.location.href=\""+hrefUrl+"\"' class='btn btn-small btn-primary btn-xs' style='background:#434A5D;border-color:#0192D7;color:white;border-radius:15px 15px;width:60px'/>";
		                        return detail;
		                    },
		                },
		    	   	  ];
		 
		 //每页显示多少行
		 var rowNum=20;
		 var page=0;
		 var url='/sensor/management/data/GRID';
		 var sort;
		 
		 //请求参数
		 var params={}
		 params['rowId']='id'
		 params['showAttributes']='serialNo,dust.serialNo,alarmQuantity,dust.frequency,maxVal,minVal,isWorked,dust.device.serialNo';//要获取的属性名
		 params['page']=page;
		 params['size']=rowNum;
		 params['sort']=sort;
		 
		 if($('#dustId').length > 0){
			params['dust.id']=$('#dustId').val();
		 }
		 
		 var myGrid = jQuery("#sensorTable");
		 var myPager = jQuery("#sensorPager");
		 
		 myGrid.jqGrid({
	    		datatype: "json",
	    		url:url,
	    		postData:params,
	    		height: '100%',
	    	   	colModel:colModel,
	    	   	multiselect: true,
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
		    	},
	    		
	    	});
		 
		 //add和edit按钮被点击
		 $("#edit").click(function(){
			 selectRowId = myGrid.getGridParam('selarrrow');
			 if(selectRowId!=null&&selectRowId!=""){
				 var func=function(){
					 if($("#sensorForm").valid()){
						 $("#sensorForm").submit();
						 return true;
					 }else{
						 return false;
					 }
				 };
				 createModalPage("配置传感器","/sensor/update?id="+selectRowId,func); 
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
		 
		//地图按钮点击事件
		$("#viewMap").click(function(){
			window.location.href='/sensor/management/map/view';
		});		 
	});