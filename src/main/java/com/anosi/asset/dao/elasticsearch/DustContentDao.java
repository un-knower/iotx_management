package com.anosi.asset.dao.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.anosi.asset.model.elasticsearch.DustContent;

public interface DustContentDao extends ElasticsearchRepository<DustContent, Long>{

}
