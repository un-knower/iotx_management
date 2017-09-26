package com.anosi.asset.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anosi.asset.dao.elasticsearch.BaseContentDao;
import com.anosi.asset.dao.elasticsearch.IotxContentDao;
import com.anosi.asset.model.elasticsearch.IotxContent;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.service.IotxContentService;

@Service("iotxContentService")
@Transactional
public class IotxContentServiceImpl extends BaseContentServiceImpl<IotxContent, String, Iotx>
		implements IotxContentService {

	@Autowired
	private IotxContentDao iotxContentDao;

	@Override
	public BaseContentDao<IotxContent, String> getRepository() {
		return iotxContentDao;
	}

	@Override
	public IotxContent save(Iotx iotx) throws Exception {
		String id = String.valueOf(iotx.getId());
		IotxContent iotxContent = getRepository().findOne(id);
		if (iotxContent == null) {
			iotxContent = new IotxContent();
			iotxContent.setId(id);
		}
		iotxContent.setCompanyName(iotx.getCompany().getName());
		iotxContent = update(iotxContent, iotx);
		return iotxContent;
	}

}
