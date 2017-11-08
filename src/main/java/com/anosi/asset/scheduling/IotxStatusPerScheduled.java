package com.anosi.asset.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.anosi.asset.service.IotxStatusPerService;

@Component
public class IotxStatusPerScheduled {

	@Autowired
	private IotxStatusPerService iotxStatusPerService;
	
	/***
	 * 每天0点保存前一天的iotx在线离线数量
	 */
	@Scheduled(cron="0 0 0 * * *")
	public void saveIotxStatusPerDaily(){
		iotxStatusPerService.saveIotxStatusPerTimer();
	}
	
}
