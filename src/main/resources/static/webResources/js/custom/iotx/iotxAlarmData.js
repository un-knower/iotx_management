/**
 * 
 */
$(document).ready(function(){
	// 创建websocket连接
	var socket = new SockJS('/endpointWisely'); //链接SockJS 的endpoint 名称为"/endpointWisely"
    var stompClient = Stomp.over(socket);//使用stomp子协议的WebSocket 客户端
    stompClient.connect('guest', 'guest', function(frame) {//链接Web Socket的服务端。
        setConnected(true);
        stompClient.subscribe('/topic/broadcast/alarmData/create', function(respnose){ //订阅频道，产生告警时会发送消息过来
        	//根据消息进行操作
        	var message = JSON.parse(respnose.body).responseMessage;//发送来的消息
        	if(message=="newAlarmData"){
        		// reload
        		getAlarmData()
        	}
        });
    });
    
    // 从服务器获取最近的告警信息
    function getAlarmData(){
    	$.ajax({
			url : '/iotxData/management/data/REMOTE',
			data : {
				'isAlarm' : true,
				'page' : 0,
				'size' : 5,
				'sort' : 'collectTime.desc',
				'showAttributes' : 'collectTime,iotxSN,level',
			},
			type : 'get',
			dataType : 'json',
			success : function(datas) {
				// 数据加载到div
				
			},
			error : function(data){
				warning('节点分布加载失败，请联系管理员或刷新页面重试');
			}
		});
    }
    
})