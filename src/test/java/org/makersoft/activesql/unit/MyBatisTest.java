/*
 * @(#)MyBatisTest.java 2013年12月23日 下午23:33:33
 *
 * Copyright (c) 2011-2013 Makersoft.org all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 */
package org.makersoft.activesql.unit;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.makersoft.activesql.mapper.UserMapper;
import org.makersoft.activesql.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@ActiveProfiles("test")
public class MyBatisTest {
	
	@Autowired(required = true)
	private UserMapper userMapper;
	
	@Test
	@Transactional
	public void testInsert() {
		User user = new User();
		user.setUserName("makersoft");
		user.setPassword("password");
		user.setBirthDay(new GregorianCalendar(1987, 12, 25).getTime());
		user.setCreatedAt(new Date());
		user.setUpdatedAt(new Date());
		
		int rows = userMapper.insertUser(user);
		
		assert rows > 0;
	}
	
	@Test
	@Transactional
	public void testDelete() {
		User user = new User();
		user.setUserName("makersoft");
		user.setPassword("password");
		user.setBirthDay(new GregorianCalendar(1987, 12, 25).getTime());
		user.setCreatedAt(new Date());
		user.setUpdatedAt(new Date());
		
		int rows = userMapper.insertUser(user);
		assert rows > 0;
		
		assertNotNull(user.getId());
		
		user = userMapper.get(new User(user.getId()));
		
		
		
		User newUser = new User(user.getId());
		newUser.setUserName("my_name");
		newUser.setUpdatedAt(user.getUpdatedAt());
		newUser.setPassword("");
		userMapper.update(newUser);
		
		user = userMapper.get(new User(user.getId()));
		
		rows = userMapper.delete(user);
		
		assertTrue(rows == 1);
	}
	
}
