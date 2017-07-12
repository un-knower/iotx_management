package com.anosi.asset.dao.jpa;

import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.QIotx;

public interface IotxDao extends BaseJPADao<Iotx>, QuerydslBinderCustomizer<QIotx> {

	@Override
	default public void customize(QuerydslBindings bindings, QIotx qIotx) {
		bindings.bind(qIotx.serialNo).first((path, value) ->  path.containsIgnoreCase(value));
	}

}
