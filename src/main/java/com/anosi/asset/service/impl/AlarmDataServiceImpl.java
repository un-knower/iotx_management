package com.anosi.asset.service.impl;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.dao.jpa.AlarmDataDao;
import com.anosi.asset.dao.jpa.BaseJPADao;
import com.anosi.asset.model.jpa.AlarmData;
import com.anosi.asset.model.jpa.QAlarmData;
import com.anosi.asset.service.AlarmDataService;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Service("alarmDataService")
@Transactional
public class AlarmDataServiceImpl extends BaseJPAServiceImpl<AlarmData> implements AlarmDataService {

	@Autowired
	private AlarmDataDao alarmDataDao;
	@Autowired
	private EntityManager entityManager;

	@Override
	public BaseJPADao<AlarmData> getRepository() {
		return alarmDataDao;
	}

	@Override
	public JSONArray getAlarmDataDaily(Pageable pageable, Predicate predicate) {
		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
		QAlarmData alarmdata = QAlarmData.alarmData;

		// 调用mysql的DATE_FORMAT函数
		StringTemplate datePath = Expressions.stringTemplate("DATE_FORMAT({0},'{1s}')", alarmdata.collectTime,
				ConstantImpl.create("%Y-%m-%d"));

		List<Tuple> alarmDataTuples = queryFactory.select(alarmdata.count(), datePath).from(alarmdata).where(predicate)
				.groupBy(datePath).orderBy(datePath.desc()).limit(pageable.getPageSize()).offset(pageable.getOffset())
				.fetch();// 按照时间倒序排列
		// 由于查询结果是越新的数据越靠前，所以需要再一次倒序
		Collections.reverse(alarmDataTuples);

		JSONArray jsonArray = new JSONArray();
		for (Tuple tuple : alarmDataTuples) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("count", tuple.get(0, Long.class));
			jsonObject.put("date", tuple.get(1, String.class));
			jsonArray.add(jsonObject);
		}

		return jsonArray;
	}

}
