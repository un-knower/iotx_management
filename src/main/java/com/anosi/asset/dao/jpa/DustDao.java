package com.anosi.asset.dao.jpa;

import javax.persistence.EntityManager;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.anosi.asset.dao.hibernateSearch.SupplyQuery;
import com.anosi.asset.model.jpa.Dust;

public interface DustDao extends BaseJPADao<Dust> {

	public Dust findBySerialNo(String serialNo);

	default public Page<Dust> findBySearchContent(EntityManager entityManager, String searchContent,
			Pageable pageable) {
		return findBySearchContent(entityManager, searchContent, pageable, Dust.class, null, "name", "serialNo", "type",
				"powerType", "iotx.company.name");
	}

	default public Page<Dust> findBySearchContent(EntityManager entityManager, String searchContent, Pageable pageable,
			String companyCode) {
		SupplyQuery supplyQuery = (queryBuilder) -> {
			return queryBuilder.bool()
					.must(queryBuilder.keyword().onFields("name", "serialNo", "type", "powerType", "iotx.company.name")
							.matching(searchContent).createQuery())
					.must(new TermQuery(new Term("iotx.company.code", companyCode))).createQuery();
		};
		return findBySearchContent(entityManager, searchContent, pageable, Dust.class, supplyQuery, "");
	}

}
