package com.anosi.asset.test;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.IotxManagementApplication;
import com.anosi.asset.model.jpa.AlarmData;
import com.anosi.asset.model.jpa.QAlarmData;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IotxManagementApplication.class)
@Transactional
public class TestAlarmData {

	@Autowired
	private EntityManager entityManager;

	@Test
	public void groupByDate() {
		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
		QAlarmData alarmdata = QAlarmData.alarmData;

		StringTemplate datePath = Expressions.stringTemplate("DATE_FORMAT({0},'{1s}')", alarmdata.collectTime,
				ConstantImpl.create("%Y-%m-%d"));

		List<Tuple> alarmDataTuples = queryFactory.select(alarmdata.count(), datePath).from(alarmdata).groupBy(datePath)
				.orderBy(datePath.desc()).fetch();
		for (Tuple tuple : alarmDataTuples) {
			System.out.println(tuple.get(0, Long.class));
			System.out.println(tuple.get(1, String.class));
		}
	}

	@Test
	public void testPage() {
		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
		QAlarmData alarmdata = QAlarmData.alarmData;

		PageRequest pageRequest = new PageRequest(0, 1);

		@SuppressWarnings("unchecked")
		List<AlarmData> alarmDatas = (List<AlarmData>) queryFactory.from(alarmdata).limit(pageRequest.getPageSize())
				.offset(pageRequest.getOffset()).fetch();
		for (AlarmData alarm : alarmDatas) {
			System.out.println(alarm.getId());
		}
	}

}
