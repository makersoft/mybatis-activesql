/*
 * @(#)MyBatisContext.java 2013年12月27日 下午23:33:33
 *
 * Copyright (c) 2011-2013 Makersoft.org all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 */
package org.makersoft.activesql.persistence;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class description goes here.
 * 
 * @version 2013年12月27日 下午1:49:31
 * @author Feng Kuok
 */
public class MyBatisContext {
	private static final MyBatisContext INSTANCE = new MyBatisContext();

	private static SqlSession sqlSession;

	public static MyBatisContext getInstance() {
		return INSTANCE;
	}

	@Autowired
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		sqlSession = sqlSessionTemplate;
	}

	// --- method ---
	public static int insert(Object parameter) {
		return sqlSession.insert(parameter.getClass().getName() + ".insert", parameter);
	}

	public static int update(Object parameter) {
		return sqlSession.update(parameter.getClass().getName() + ".update", parameter);
	}

	public static int delete(Object parameter) {
		return sqlSession.delete(parameter.getClass().getName() + ".delete", parameter);
	}

	// --- query method ---
	public static <T> T find(Class<T> clazz, Long id) {
		return sqlSession.selectOne(clazz.getName() + ".getById", id);
	}

//	public static List<? extends ActiveRecord> find(Class<?> clazz, Long... ids) {
//		return sqlSession.selectList(clazz.getName() + ".findByIds", ids);
//	}
//
//	public static long count(Class<?> clazz) {
//		return sqlSession.selectOne(clazz.getName() + ".count");
//	}
//
//	public static List<?> findAll(Class<?> clazz) {
//		return sqlSession.selectList(clazz.getName() + ".findAll");
//	}
//
//	public static List<? extends ActiveRecord> findEntityEntries(Class<?> clazz, int firstResult,
//			int maxResults) {
//		return sqlSession.selectList(clazz.getName() + ".findAll", null, new RowBounds(firstResult,
//				maxResults));
//	}
}
