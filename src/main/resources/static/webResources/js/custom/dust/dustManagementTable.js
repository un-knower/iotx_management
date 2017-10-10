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
		               	{label:'名称',name:'name',index:'name', width: '120', sortable:false, align: 'center'},
						{label:'采集频率',name:'frequency',index:'frequency', width: '120', sortable:false, align: 'center'},
						{label:'传感器数量',name:'sensorQuantity',index:'sensorQuantity', width: '120', sortable:false, align: 'center'},
						{label:'种类',name:'type', index:'type',sortable: true, width:'120', align: 'center'},
						{label:'电源种类',name:'powerType', index:'powerType',sortable: true, width:'120', align: 'center'},
						{label:'配置文件编号',name:'configId', index:'configId',sortable: true, width:'120', align: 'center'},
						{label:'所属iotx',name:'iotx.serialNo', index:'iotx.serialNo',sortable: true, width:'120', align: 'center'},
						{label:'连接设备序列号',name:'device.serialNo', index:'device.serialNo',sortable: true, width:'120', align: 'center'},
						{label:'状态',name:'isWorked', index:'isWorked',sortable: true, width:'120', align: 'center'},
						{
			                label:'操作', name: 'operate', index: 'operate', width: 90, align:'center',
			                formatter: function (cellvalue, options, rowObject) {
			                	var hrefUrl='/dust/management/detail/'+options.rowId+"/view";
			                    var detail="<input value='详情' type='button' onclick='window.location.href=\""+hrefUrl+"\"' class='btn btn-small btn-primary btn-xs' style='background:#434A5D;border-color:#0192D7;color:white;border-radius:15px 15px;width:80px'/>";
			                    return detail;
			                },
			            },
				   	  ];
		 
		 //每页显示多少行
		 var rowNum=20;
		 var page=0;
		 var url='/dust/management/data/GRID';
		 var sort;
		 var selectRowId;
		 
		 //请求参数
		 var params={}
		 params['rowId']='id'
		 params['showAttributes']='serialNo,name,frequency,sensorQuantity,type,powerType,configId,iotx.serialNo,device.serialNo,isWorked';//要获取的属性名
		 params['page']=page;
		 params['size']=rowNum;
		 params['sort']=sort;
		 
		 if($('#iotxId').length > 0){
			 params['iotx.id']=$('#iotxId').val();
		 }
		 
		 var myGrid = jQuery("#dustTable");
		 var myPager = jQuery("#dustPager");
		 
		 myGrid.jqGrid({
	    		datatype: "json",
	    		url:url,
	    		postData:params,
	    		height: '100%',
	    	   	colModel:colModel,
	    	   	caption:'dust管理',
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
		    	}
		    	
	     });
		 
		 //add和edit按钮被点击
		 $("#edit").click(function(){
			 selectRowId = myGrid.getGridParam('selarrrow');
			 if(selectRowId!=null&&selectRowId!=""){
				 var func=function(){
					 if($("#dustForm").valid()){
						 $("#dustForm").submit();
						 return true;
					 }else{
						 return false;
					 }
				 };
				 createModalPage("配置dust节点","/dust/update?id="+selectRowId,func); 
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
			window.location.href='/dust/management/map/view';
		});
		 
	});