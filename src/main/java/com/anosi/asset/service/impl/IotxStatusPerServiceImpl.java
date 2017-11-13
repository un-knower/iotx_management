package com.anosi.asset.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.dao.jpa.IotxStatusPerDao;
import com.anosi.asset.model.jpa.Company;
import com.anosi.asset.model.jpa.IotxStatusPer;
import com.anosi.asset.model.jpa.Iotx.Status;
import com.anosi.asset.service.CompanyService;
import com.anosi.asset.service.IotxService;
import com.anosi.asset.service.IotxStatusPerService;

@Service("iotxStatusPerService")
@Transactional
public class IotxStatusPerServiceImpl extends BaseJPAServiceImpl<IotxStatusPer> implements IotxStatusPerService {

	@Autowired
	private IotxStatusPerDao iotxStatusPerDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private IotxService iotxService;

	@Override
	public BaseJPADao<IotxStatusPer> getRepository() {
		return iotxStatusPerDao;
	}

	@Override
	public void saveIotxStatusPerTimer() {
		List<Company> companys = companyService.findAll();
		List<IotxStatusPer> iotxStatusPers = companys.stream().map(company -> {
			IotxStatusPer iotxStatusPer = new IotxStatusPer();

			iotxStatusPer.setCompany(company);
			// 按照公司获取在线和离线的数量
			iotxStatusPer.setOnline(iotxService.countByCompanyAndStatus(company.getId(), Status.ONLINE));
			iotxStatusPer.setOffline(iotxService.countByCompanyAndStatus(company.getId(), Status.OFFLINE));

			// 获取前一天
			Calendar cal = Calendar.getInstance();// 得到一个Calendar的实例
			cal.setTime(new Date()); // 设置时间为当前时间
			cal.add(Calendar.DATE, -1); // 日期减1
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date lastDate = cal.getTime();
			iotxStatusPer.setCountDate(lastDate);

			return iotxStatusPer;
		}).collect(Collectors.toList());
		iotxStatusPerDao.save(iotxStatusPers);
	}

}
