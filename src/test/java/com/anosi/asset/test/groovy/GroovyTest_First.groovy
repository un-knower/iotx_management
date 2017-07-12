package com.anosi.asset.test.groovy

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

import com.anosi.asset.IotxManagementApplication
import com.anosi.asset.service.AccountService

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IotxManagementApplication.class)
@Transactional
class GroovyTest_First {
	
	private static final Logger logger = LoggerFactory.getLogger(GroovyTest_First.class);

	@Autowired
	private AccountService accountService;
	
	@Test
	public void testCacheByFindAccount(){
		logger.debug("this is first query");
		def account=accountService.findByLoginId("admin");
		logger.debug('account.loginId:{}',account.loginId)
		logger.debug('account.name:{}',account.name)
	}
	
}
