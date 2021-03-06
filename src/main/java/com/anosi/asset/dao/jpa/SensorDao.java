package com.anosi.asset.dao.jpa;

import java.util.ArrayList;

import javax.persistence.EntityManager;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import com.anosi.asset.dao.hibernateSearch.SupplyQuery;
import com.anosi.asset.model.jpa.QSensor;
import com.anosi.asset.model.jpa.Sensor;
import com.google.common.collect.Lists;

public interface SensorDao extends BaseJPADao<Sensor>,QuerydslBinderCustomizer<QSensor>  {

	@EntityGraph(attributePaths = { "sensorCategory", "dust" })
	public Sensor findBySerialNoEquals(String serialNo);
	
	@Override
	default public void customize(QuerydslBindings bindings, QSensor qSensor) {
		bindings.bind(qSensor.serialNo).first((path, value) -> {
			if (value.startsWith("like$")) {
				value = value.replace("like$", "");
				return path.startsWithIgnoreCase(value);
			} else {
				return path.eq(value);
			}
		});
		// 语法糖是这样的：size为1,代表等于这个值;如果size为2且都非负值,代表在这个区间;如果size为2有一负值,代表大于非负值
		bindings.bind(qSensor.alarmQuantity).all((path, values) -> {
			ArrayList<? extends Long> valueList = Lists.newArrayList(values);
			if (values.size() == 1) {
				return path.eq(valueList.get(0));
			} else if (values.size() == 2) {
				valueList.sort((a, b) -> a.compareTo(b));
				if (valueList.get(0) >= 0 && valueList.get(1) >= 0) {
					return path.between(valueList.get(0), valueList.get(1));
				} else {
					return path.gt(valueList.get(1));
				}
			}
			return null;
		});
	}

	default public Page<Sensor> findBySearchContent(EntityManager entityManager, String searchContent,
			Pageable pageable) {
		return findBySearchContent(entityManager, searchContent, pageable, Sensor.class, null, "name",
				"parameterDescribe", "serialNo", "dust.iotx.company.name");
	}

	default public Page<Sensor> findBySearchContent(EntityManager entityManager, String searchContent,
			Pageable pageable, String companyCode) {
		SupplyQuery supplyQuery = (queryBuilder) -> {
			return queryBuilder.bool()
					.must(queryBuilder.keyword()
							.onFields("name", "parameterDescribe", "serialNo", "dust.iotx.company.name")
							.matching(searchContent).createQuery())
					.must(new TermQuery(new Term("dust.iotx.company.code", companyCode))).createQuery();
		};
		return findBySearchContent(entityManager, searchContent, pageable, Sensor.class, supplyQuery, "");
	}

}
