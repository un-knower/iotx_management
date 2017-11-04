package com.anosi.asset.dao.jpa;

import javax.persistence.EntityManager;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import com.anosi.asset.dao.hibernateSearch.SupplyQuery;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.QIotx;

public interface IotxDao extends BaseJPADao<Iotx>, QuerydslBinderCustomizer<QIotx> {

	@Override
	default public void customize(QuerydslBindings bindings, QIotx qIotx) {
		bindings.bind(qIotx.serialNo).first((path, value) -> path.containsIgnoreCase(value));
	}

	public Iotx findBySerialNo(String serialNo);

	default public Page<Iotx> findBySearchContent(EntityManager entityManager, String searchContent,
			Pageable pageable) {
		return findBySearchContent(entityManager, searchContent, pageable, Iotx.class, null, "serialNo", "company.name",
				"district.name", "district.city.name", "district.city.province.name");
	}

	default public Page<Iotx> findBySearchContent(EntityManager entityManager, String searchContent, Pageable pageable,
			String companyName) {
		SupplyQuery supplyQuery = (queryBuilder) -> {
			return queryBuilder.bool()
					.must(queryBuilder.keyword()
							.onFields("serialNo", "company.name", "district.name", "district.city.name",
									"district.city.province.name")
							.matching(searchContent).createQuery())
					.must(new TermQuery(new Term("company.name", companyName))).createQuery();
		};
		return findBySearchContent(entityManager, searchContent, pageable, Iotx.class, supplyQuery, "");
	}

}
