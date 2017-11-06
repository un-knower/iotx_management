package com.anosi.asset.test;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.IotxManagementApplication;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.QAccount;
import com.anosi.asset.model.jpa.QRole;
import com.anosi.asset.model.jpa.Role;
import com.anosi.asset.model.mongo.IotxData;
import com.anosi.asset.model.mongo.QIotxData;
import com.anosi.asset.util.DateFormatUtil;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IotxManagementApplication.class)
@Transactional
public class QuerydslTest {
	
	private static final Logger logger = LoggerFactory.getLogger(QuerydslTest.class);
	
	private QIotxData qIotxData;
	
	private QAccount qAccount;
	
	private QRole qRole;
	
	private SpringDataMongodbQuery<IotxData> mongodbQuery;
	
	@Autowired
	private EntityManager entityManager;
	
	private JPAQueryFactory queryFactory;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Before
	public void init(){
		qIotxData=QIotxData.iotxData;
		mongodbQuery = new SpringDataMongodbQuery<IotxData>(mongoTemplate, IotxData.class);
		
		qAccount=QAccount.account;
		qRole=QRole.role;
		queryFactory = new JPAQueryFactory(entityManager);
	}
	
	@Test
	public void testQueryIotxData(){
		IotxData iotxData = mongodbQuery.where(qIotxData.val.eq(99.0)).fetchFirst();
		logger.debug(iotxData.getSensorSN());
		logger.debug(DateFormatUtil.getFormateDate(iotxData.getCollectTime()));
	}
	
	@Test
	public void testReflactMethod() throws Exception{
		Class<?> clazz = SpringDataMongodbQuery.class;
		Method method = clazz.getSuperclass().getDeclaredMethod("where",Array.newInstance(Predicate.class, 0).getClass());
		Object objs = Array.newInstance(Predicate.class, 2);
        Array.set(objs, 0, qIotxData.val.eq(99.0));
        @SuppressWarnings("unchecked")
		SpringDataMongodbQuery<IotxData> invoke = (SpringDataMongodbQuery<IotxData>) method.invoke(mongodbQuery, objs);
        IotxData iotxData =invoke.fetchFirst();
        logger.debug(iotxData.getSensorSN());
		logger.debug(DateFormatUtil.getFormateDate(iotxData.getCollectTime()));
	}
	
	@Test
	public void testQueryAccount(){
		Account account = (Account) queryFactory.from(qAccount).where(qAccount.name.contains("ad")).fetchFirst();
		logger.debug(account.getName());
		logger.debug(account.getLoginId());
	}
	
	@Test
	public void testQueryRole(){
		List<Role> roles = queryFactory.select(qRole).from(qAccount,qRole).where(qRole.accountList.contains(qAccount),(qAccount.loginId.eq("admin"))).fetch();
		for (Role role : roles) {
			logger.debug(String.valueOf(role.getId()));
			logger.debug(role.getRoleCode());
		}
	}
	
}
