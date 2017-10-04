/**
 * 
 */
$(document).ready(function() {
	$("#iotxForm").validate({
		//debug:true,
		rules : {
			
		},
		submitHandler: function(form) {  
			var options = {
				type : "post",
				url : '/iotx/update',
				success : function(data) {
					$.unblockUI();
					if(data.result=='success'){
						info('操作成功');
						$("#iotxTable").trigger("reloadGrid");
					}else if(data.result=='error'){
						warning('操作失败:'+data.message);
					}
				}
			};
			
			$.blockUI({message: '<img src="/webResources/img/loading/loading.gif" /> '});
			$(form).ajaxSubmit(options);     
		}  
	});

})
