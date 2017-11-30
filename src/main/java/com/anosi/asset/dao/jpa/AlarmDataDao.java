package com.anosi.asset.dao.jpa;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import com.anosi.asset.model.jpa.AlarmData;
import com.anosi.asset.model.jpa.QAlarmData;
import com.querydsl.core.types.dsl.PathInits;

public interface AlarmDataDao extends BaseJPADao<AlarmData>,QuerydslBinderCustomizer<QAlarmData>{
	
	public static final Logger logger = LoggerFactory.getLogger(AlarmDataDao.class);
	
	@Override
	default public void customize(QuerydslBindings bindings, QAlarmData qAlarmData) {
		PathInits inits = new PathInits("sensor.dust.device.serialNo");
		qAlarmData = new QAlarmData(AlarmData.class, forVariable("alarmData"), inits);
		bindings.bind(qAlarmData.sensor.dust.device.serialNo).first((path, value) -> {
			logger.info("{}",path);
			if (value.startsWith("like$")) {
				value = value.replace("like$", "");
				return path.startsWithIgnoreCase(value);
			} else {
				return path.eq(value);
			}
		});
	}

}
