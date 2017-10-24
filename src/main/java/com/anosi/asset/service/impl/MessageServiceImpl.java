package com.anosi.asset.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.dao.mongo.BaseMongoDao;
import com.anosi.asset.dao.mongo.MessageDao;
import com.anosi.asset.model.mongo.Message;
import com.anosi.asset.service.MessageService;

@Service("messageService")
@Transactional
public class MessageServiceImpl extends BaseMongoServiceImpl<Message> implements MessageService {
	
	@Autowired
	private MessageDao messageDao;

	@Override
	public BaseMongoDao<Message> getRepository() {
		return messageDao;
	}

}
