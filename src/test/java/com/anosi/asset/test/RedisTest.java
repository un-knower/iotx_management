package com.anosi.asset.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.IotxManagementApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IotxManagementApplication.class)
@Transactional
public class RedisTest {
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Test
	public void testRedis(){
		ValueOperations<String, String> opv = redisTemplate.opsForValue();
		opv.set("testKey1", "testValue1");
	}
	
}
