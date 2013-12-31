///*
// * @(#)ActiveSQLPlugin.java 2013年12月23日 下午23:33:33
// *
// * Copyright (c) 2011-2013 Makersoft.org all rights reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// *
// */
//package org.makersoft.activesql.plugin;
//
//import java.io.IOException;
//import java.lang.reflect.Field;
//import java.sql.Statement;
//import java.util.Map;
//import java.util.Properties;
//import java.util.Set;
//import java.util.concurrent.locks.ReentrantLock;
//
//import org.apache.ibatis.executor.BaseExecutor;
//import org.apache.ibatis.executor.CachingExecutor;
//import org.apache.ibatis.executor.statement.StatementHandler;
//import org.apache.ibatis.plugin.Interceptor;
//import org.apache.ibatis.plugin.Intercepts;
//import org.apache.ibatis.plugin.Invocation;
//import org.apache.ibatis.plugin.Plugin;
//import org.apache.ibatis.plugin.Signature;
//import org.apache.ibatis.session.Configuration;
//import org.apache.ibatis.session.ResultHandler;
//import org.makersoft.activesql.annotations.Entity;
//import org.makersoft.activesql.builder.GenericStatementBuilder;
//import org.scannotation.AnnotationDB;
//import org.scannotation.ClasspathUrlFinder;
//
///**
// * 
// */
//@Intercepts({ @Signature(type = StatementHandler.class, method = "query", args = { Statement.class,
//		ResultHandler.class }) })
//public class ActiveSQLPlugin implements Interceptor {
//	
//	private volatile boolean initialize = false;
//
//	@Override
//	public Object intercept(Invocation invocation) throws Throwable {
//		// MappedStatement ms = (MappedStatement)invocation.getArgs()[0];
//		// Object obj = (Object)invocation.getArgs()[1];
//		// RowBounds rowBounds = (RowBounds)invocation.getArgs()[2];
//
//		// /Statement statement = (Statement) invocation.getArgs()[0];
//
//		return invocation.proceed();
//	}
//
//	@Override
//	public Object plugin(Object target) {
//		System.out.println("+++++++++++++++++");
//		if(!initialize){
//			synchronized (this) {
//				if(!initialize){
//					try {
//						BaseExecutor executor;
//						if (target instanceof CachingExecutor) {
//							Field delegateField = CachingExecutor.class.getDeclaredField("delegate");
//							delegateField.setAccessible(true);
//							executor = (BaseExecutor) delegateField.get(target);
//						} else {
//							executor = (BaseExecutor) target;
//						}
//		
//						Field configurationField = BaseExecutor.class.getDeclaredField("configuration");
//						configurationField.setAccessible(true);
//						Configuration configuration = (Configuration) configurationField.get(executor);
//		
//						for (String clazzName : findEntityClassNames()) {
//							GenericStatementBuilder builder = new GenericStatementBuilder(configuration, Class.forName(clazzName));
//							builder.build();
//						}
//		
//					} catch (Exception e) {
//						throw new RuntimeException("Initialize ActiveSQL plugin error.", e);
//					}
//					
//					initialize = true;
//				}
//			}
//		}
//
//		return Plugin.wrap(target, this);
//	}
//
//	@Override
//	public void setProperties(Properties properties) {
//		
//	}
//	
//	private Set<String> findEntityClassNames() throws IOException{
//		AnnotationDB annotationDB = new AnnotationDB();
//		annotationDB.setScanClassAnnotations(true);
//		annotationDB.scanArchives(ClasspathUrlFinder.findClassPaths());
//		Map<String, Set<String>> map = annotationDB.getAnnotationIndex();
//		Set<String> classNames = map.get(Entity.class.getName());
//		
//		return classNames;
//	}
//
//}
