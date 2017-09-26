package com.anosi.asset.dao.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.anosi.asset.model.elasticsearch.IotxContent;


public interface IotxContentDao  extends ElasticsearchRepository<IotxContent, Long> {

}
