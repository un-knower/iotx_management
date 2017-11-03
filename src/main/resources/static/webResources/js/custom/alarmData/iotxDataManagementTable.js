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
		
		//每页显示多少行
		 var rowNum=20;
		 var page=0;
		 var url='/iotxData/management/data/GRID';
		 var sort;
		 
		 //请求参数
		 var params={}
		 //设置请求需要的一些参数
		 params['rowId']='stringId'
		 params['showAttributes']='iotxSN,dustSN,sensorSN,deviceSN,val,maxVal,minVal,message,level,collectTime,closeTime';//要获取的属性名
		 params['alarm']=true;
		 params['page']=page;
		 params['size']=rowNum;
		 params['sort']=sort;
		 
		 if($("#sensorSN")){
			 params['sensorSN']=$("#sensorSN").val();
		 }

		
		 var colModel=[
           	{label:$.i18n.prop('iotxData.iotxSerialNo'),name:'iotxSN', index:'iotxSN', width:'120', sortable: false,align: 'center'},
           	{label:$.i18n.prop('iotxData.dustSerialNo'),name:'dustSN', index:'dustSN', width:'120', sortable: false,align: 'center'},
			{label:$.i18n.prop('iotxData.sensorSerialNo'),name:'sensorSN',index:'sensorSN', width: '120', sortable:false, align: 'center'},
			{label:$.i18n.prop('iotxData.deviceSerialNo'),name:'deviceSN',index:'deviceSN', width: '120', sortable:false, align: 'center'},
			{label:$.i18n.prop('iotxData.val'),name:'val', index:'val', width:'120', sortable: false,align: 'center'},
			{label:$.i18n.prop('iotxData.maxVal'),name:'maxVal', index:'maxVal', width:'120', sortable: false,align: 'center'},
			{label:$.i18n.prop('iotxData.minVal'),name:'minVal', index:'minVal', width:'120', sortable: false,align: 'center'},
			{label:$.i18n.prop('iotxData.message'),name:'message', index:'message', width:'120', sortable: false,align: 'center'},
			{label:$.i18n.prop('iotxData.level'),name:'level', index:'level', width:'120', sortable: false,align: 'center'},
			{label:$.i18n.prop('iotxData.collectTime'),name:'collectTime',index:'collectTime',collectTime:'minVal', width:'120', sortable: false,align: 'center',
				formatter:'date', 
				formatoptions:{srcformat: 'Y-m-d H:i:s', newformat: 'Y-m-d H:i:s'}
			},
			{label:$.i18n.prop('iotxData.closeTime'),name:'closeTime',index:'closeTime',collectTime:'minVal', width:'120', sortable: false,align: 'center',
				formatter:'date', 
				formatoptions:{srcformat: 'Y-m-d H:i:s', newformat: 'Y-m-d H:i:s'}
			},
			{
                label:$.i18n.prop('operate'), name: 'operate', index: 'operate', width: 120, align:'center',
                formatter: function (cellvalue, options, rowObject) {
                	var detail;
                	var closeTimeIndex=params['showAttributes'].split(",").indexOf('closeTime');
                	if(rowObject[closeTimeIndex]==null){
                		detail="<img id='open0' height='35' width='35' src='/webResources/img/icon/uncomfirm.png'/>&nbsp;&nbsp;&nbsp;<button  class='btn btn-small btn-primary btn-xs' style='margin:4px;' id='fileId5' confirm='"+options.rowId+"'>确认</button>"
                	}else{
                		detail="<img id='open1' height='35' width='35' src='/webResources/img/icon/confirm.png'>&nbsp;&nbsp;&nbsp;<button  class='btn btn-small btn-primary btn-xs' style='margin:4px;' id='fileId5' >确认</button>" 
                	}
                    return detail;
                },
            },
	   	  ];
		 
		 var myGrid = jQuery("#iotxDataTable");
		 var myPager = jQuery("#iotxDataPager");
		 
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
		 
		 //查询按钮点击事件
		 $('#searchBtn').click(function(){
			var search = $('#toSearch').val();
			params['searchContent']=search;
			myGrid.jqGrid().setGridParam({
				url:url,
				postData:params,
			}).trigger("reloadGrid");
		 })
		 
		 $(document).on("click","button[confirm]",function(){
			 $.ajax({
				type:"post",
				url:'/iotxData/save',
				data : {
					'iotxDataId' : $(this).attr("confirm"),
					'closeTime' : new Date().format("yyyy-MM-dd HH:mm:ss"),
				},
				async:true,
				success:function(data){
					myGrid.jqGrid().setGridParam({
						url:url,
						postData:params,
					}).trigger("reloadGrid");
				}
			 });
		 });
		 
	});