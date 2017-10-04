/**
 * 
 */
$(document).ready(function() {
	$("#dustForm").validate({
		//debug:true,
		rules : {
			name : {
				required : true,
			},
			frequency : {
				required : true,
			},
		},
		submitHandler: function(form) {  
			var options = {
				type : "post",
				url : '/dust/update',
				success : function(data) {
					$.unblockUI();
					if(data.result=='success'){
						info('操作成功');
						$("#dustTable").trigger("reloadGrid");
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
