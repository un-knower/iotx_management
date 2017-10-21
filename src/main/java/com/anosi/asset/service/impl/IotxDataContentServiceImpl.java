package com.anosi.asset.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.dao.elasticsearch.BaseContentDao;
import com.anosi.asset.dao.elasticsearch.IotxDataContentDao;
import com.anosi.asset.model.elasticsearch.IotxDataContent;
import com.anosi.asset.model.mongo.IotxData;
import com.anosi.asset.service.IotxDataContentService;

@Service("iotxDataContentService")
@Transactional
public class IotxDataContentServiceImpl extends BaseContentServiceImpl<IotxDataContent, String, IotxData>
		implements IotxDataContentService {

	@Autowired
	private IotxDataContentDao iotxDataContentDao;

	@Override
	public BaseContentDao<IotxDataContent, String> getRepository() {
		return iotxDataContentDao;
	}

	@Override
	public IotxDataContent saveContent(IotxData iotxData) throws Exception {
		String id = String.valueOf(iotxData.getId());
		IotxDataContent iotxDataContent = iotxDataContentDao.findOne(id);
		if (iotxDataContent == null) {
			iotxDataContent = new IotxDataContent();
			iotxDataContent.setId(id);
		}
		iotxDataContent.setCompanyName(iotxData.getCompanyName());
		iotxDataContent.setAlarmStatus(iotxData.isAlarm());
		iotxDataContent = setCommonContent(iotxDataContent, iotxData);
		return iotxDataContentDao.save(iotxDataContent);
	}

	@Override
	public Page<IotxDataContent> findByContentAndAlarm(String content, boolean isAlarm, Pageable pageable) {
		return iotxDataContentDao.findByContentContainingAndAlarmStatusEquals(content, isAlarm, pageable);
	}

	@Override
	public Page<IotxDataContent> findByContentAndAlarmAndCompanyName(String content, boolean isAlarm,
			String companyName, Pageable pageable) {
		return iotxDataContentDao.findByContentContainingAndAlarmStatusAndCompanyNameEquals(content, isAlarm,
				companyName, pageable);
	}

	@Override
	public <S extends IotxData> Iterable<IotxDataContent> saveContent(Iterable<S> obs) throws Exception {
		List<IotxDataContent> iotxDataContents = new ArrayList<>();
		for (IotxData iotxData : obs) {
			String id = String.valueOf(iotxData.getId());
			IotxDataContent iotxDataContent = iotxDataContentDao.findOne(id);
			if (iotxDataContent == null) {
				iotxDataContent = new IotxDataContent();
				iotxDataContent.setId(id);
			}
			iotxDataContent.setCompanyName(iotxData.getCompanyName());
			iotxDataContent.setAlarmStatus(iotxData.isAlarm());
			iotxDataContent = setCommonContent(iotxDataContent, iotxData);
			iotxDataContents.add(iotxDataContent);
		}
		return iotxDataContentDao.save(iotxDataContents);
	}

}
