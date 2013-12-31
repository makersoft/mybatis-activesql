///*
// * @(#)DeleteStatementBuilder.java 2013年12月25日 下午23:33:33
// *
// * Copyright (c) 2011-2013 Makersoft.org all rights reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// *
// */
//package org.makersoft.activesql.builder.impl;
//
//import java.lang.reflect.Field;
//
//import org.apache.commons.lang3.StringUtils;
//import org.apache.ibatis.builder.MapperBuilderAssistant;
//import org.apache.ibatis.executor.keygen.KeyGenerator;
//import org.apache.ibatis.executor.keygen.NoKeyGenerator;
//import org.apache.ibatis.mapping.SqlCommandType;
//import org.apache.ibatis.mapping.SqlSource;
//import org.apache.ibatis.mapping.StatementType;
//import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
//import org.apache.ibatis.scripting.xmltags.SqlNode;
//import org.apache.ibatis.scripting.xmltags.TextSqlNode;
//import org.apache.ibatis.session.Configuration;
//import org.makersoft.activesql.annotations.Id;
//import org.makersoft.activesql.builder.StatementBuilder;
//import org.makersoft.activesql.utils.CaseFormatUtils;
//
///**
// * Class description goes here.
// * 
// */
//public class DeleteStatementBuilder extends StatementBuilder{
//
//	private Configuration configuration;
//	private MapperBuilderAssistant assistant;
//	private Class<?> entityClass;
//	
//	private Field idField;
//	private String tableName;
//	
//	public DeleteStatementBuilder(Configuration configuration) {
//
//	}
//	
//	
//	@Override
//	public void build(){
//		Integer timeout = null;
//		Class<?> parameterType = entityClass;
//		
//		//~~~~~~~~~~~~~~~~~~~~~~~
//		boolean flushCache = true;
//		boolean useCache = false;
//		boolean resultOrdered = false;
//		KeyGenerator keyGenerator = new NoKeyGenerator();
//		
//		Id id = idField.getAnnotation(Id.class);
//		
//		String idFieldName = idField.getName();
//		String idColumn = StringUtils.isNotBlank(id.column()) ? id.column() : CaseFormatUtils.camelToUnderScore(idFieldName);
//		
//		String versionSQL = getVersionSQL();
//		
//		SqlNode sqlNode = new TextSqlNode("DELETE FROM " + tableName + " WHERE " + idColumn + " = #{" + idFieldName + "} " + versionSQL);
//		
//		SqlSource sqlSource = new DynamicSqlSource(configuration, sqlNode);
//		
//		assistant.addMappedStatement("delete", sqlSource, StatementType.PREPARED,
//				SqlCommandType.DELETE, null, timeout, null, parameterType, null, null, null,
//				flushCache, useCache, resultOrdered, keyGenerator, null, null, configuration.getDatabaseId(), configuration.getDefaultScriptingLanuageInstance());
//	}
//	
//}
