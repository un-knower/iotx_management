package com.anosi.asset.dao.jpa;

import javax.persistence.EntityManager;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import com.anosi.asset.dao.hibernateSearch.SupplyQuery;
import com.anosi.asset.model.jpa.Sensor;

public interface SensorDao extends BaseJPADao<Sensor> {

	@EntityGraph(attributePaths = { "sensorCategory", "dust" })
	public Sensor findBySerialNoEquals(String serialNo);

	default public Page<Sensor> findBySearchContent(EntityManager entityManager, String searchContent,
			Pageable pageable) {
		return findBySearchContent(entityManager, searchContent, pageable, Sensor.class, null, "name",
				"parameterDescribe", "serialNo", "dust.iotx.company.name");
	}

	default public Page<Sensor> findBySearchContent(EntityManager entityManager, String searchContent,
			Pageable pageable, String companyName) {
		SupplyQuery supplyQuery = (queryBuilder) -> {
			return queryBuilder.bool()
					.must(queryBuilder.keyword()
							.onFields("name", "parameterDescribe", "serialNo", "dust.iotx.company.name")
							.matching(searchContent).createQuery())
					.must(new TermQuery(new Term("dust.iotx.company.name", companyName))).createQuery();
		};
		return findBySearchContent(entityManager, searchContent, pageable, Sensor.class, supplyQuery, "");
	}

}
