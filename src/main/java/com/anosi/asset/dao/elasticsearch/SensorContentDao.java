package com.anosi.asset.dao.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.anosi.asset.model.elasticsearch.SensorContent;

public interface SensorContentDao extends ElasticsearchRepository<SensorContent, Long>{

}
