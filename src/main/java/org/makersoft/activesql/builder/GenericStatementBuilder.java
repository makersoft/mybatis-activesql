/*
 * @(#)GenericStatementBuilder.java 2013年12月23日 下午23:33:33
 *
 * Copyright (c) 2011-2013 Makersoft.org all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 */
package org.makersoft.activesql.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.IfSqlNode;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SetSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;
import org.apache.ibatis.scripting.xmltags.TrimSqlNode;
import org.apache.ibatis.session.Configuration;
import org.makersoft.activesql.annotations.Column;
import org.makersoft.activesql.annotations.Delete;
import org.makersoft.activesql.annotations.Entity;
import org.makersoft.activesql.annotations.Id;
import org.makersoft.activesql.annotations.Insert;
import org.makersoft.activesql.annotations.Select;
import org.makersoft.activesql.annotations.Table;
import org.makersoft.activesql.annotations.Transient;
import org.makersoft.activesql.annotations.Update;
import org.makersoft.activesql.annotations.Version;
import org.makersoft.activesql.utils.AnnotationUtils;
import org.makersoft.activesql.utils.CaseFormatUtils;
import org.makersoft.activesql.utils.ReflectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;

/**
 * Class description goes here.
 * 
 */
public class GenericStatementBuilder extends BaseBuilder {

	private MapperBuilderAssistant assistant;

	private Class<?> entityClass;
	
	private String databaseId;
	private LanguageDriver lang;

	////////~~~~~~~~~~~~~~~~~
	private String tableName;
	
	////////~~~~~~~~~~~~~~~~~
	private Field idField;
	
	private Field versionField;
	
	private List<Field> columnFields = new ArrayList<Field>();
	
	//
	private Class<?> mapperType;
	
	public GenericStatementBuilder(Configuration configuration, Class<?> entityClass) {
		super(configuration);
		this.entityClass = entityClass;
		
		String resource = entityClass.getName().replace('.', '/') + ".java (best guess)";
		assistant = new MapperBuilderAssistant(configuration, resource);
		
		mapperType = entityClass.getAnnotation(Entity.class).mapper();
		
		if(!mapperType.isAssignableFrom(Void.class)) {
			assistant.setCurrentNamespace(mapperType.getName());
		} else {
			assistant.setCurrentNamespace(entityClass.getName());
		}
		
		databaseId = super.getConfiguration().getDatabaseId();
		lang = super.getConfiguration().getDefaultScriptingLanuageInstance();
		
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~
		Table table = entityClass.getAnnotation(Table.class);
		if(table == null){
			tableName = CaseFormatUtils.camelToUnderScore(entityClass.getSimpleName());
		}else {
			tableName = table.name();
		}
		
		///~~~~~~~~~~~~~~~~~~~~~~
		idField = AnnotationUtils.findDeclaredFieldWithAnnoation(Id.class, entityClass);
		
		versionField = AnnotationUtils.findDeclaredFieldWithAnnoation(Version.class, entityClass);
		
		ReflectionUtils.doWithFields(entityClass, new FieldCallback() {
			
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				columnFields.add(field);
			}
		}, new FieldFilter() {
			
			@Override
			public boolean matches(Field field) {
				if(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
					return false;
				}
				
				for(Annotation annotation : field.getAnnotations()){
					if(Transient.class.isAssignableFrom(annotation.getClass()) 
							|| Id.class.isAssignableFrom(annotation.getClass())){
						return false;
					}
				}
				
				return true;
			}
		});
	}
	
	protected String getColumnNameByField(Field field){
		Column column = field.getAnnotation(Column.class);
		if(column == null){
			return CaseFormatUtils.camelToUnderScore(field.getName());
		}else {
			return StringUtils.isNotBlank(column.name()) ? column.name() : CaseFormatUtils.camelToUnderScore(field.getName());
		}
	}
	
	protected String getTestByField(Field field) {
		Column column = field.getAnnotation(Column.class);
		if(column != null && StringUtils.isNotBlank(column.test())){
			return column.test();
		} else {
			return field.getName() + "!= null";
		}
		
	}
	
	private String getIdColumnName() {
		Id id = idField.getAnnotation(Id.class);
		return StringUtils.isNotBlank(id.column()) ? id.column() : CaseFormatUtils.camelToUnderScore(idField.getName());
	}
	
	private String getIdFieldName() {
		return idField.getName();
	}
	
	private String getVersionSQL() {
		if(versionField != null){
			return " AND " + getColumnNameByField(versionField) + " = #{" + versionField.getName() + "}";
		}
		
		return StringUtils.EMPTY;
	}
	
	public void build() {
		String insertStatementId = "insert";
		String deleteStatementId = "delete";
		String updateStatementId = "update";
		String selectStatementId = "select";
		
		if(!mapperType.isAssignableFrom(Void.class)) {
			List<Method> insertMethods = ReflectUtils.findMethodsAnnotatedWith(mapperType, Insert.class);
			if(insertMethods != null && insertMethods.size() > 0) {
				if(insertMethods.size() > 1) {
					throw new RuntimeException("有多个@Insert方法");
				}
				insertStatementId = insertMethods.get(0).getName();
			}
			
			List<Method> deleteMethods = ReflectUtils.findMethodsAnnotatedWith(mapperType, Delete.class);
			if(deleteMethods != null && deleteMethods.size() > 0) {
				if(deleteMethods.size() > 1) {
					throw new RuntimeException("有多个@Delete方法");
				}
				deleteStatementId = deleteMethods.get(0).getName();
			}
			
			List<Method> updateMethods = ReflectUtils.findMethodsAnnotatedWith(mapperType, Update.class);
			if(updateMethods != null) {
				if(updateMethods.size() > 1 && updateMethods.size() > 0) {
					throw new RuntimeException("有多个@Update方法");
				}
				updateStatementId = updateMethods.get(0).getName();
			}
			
			List<Method> selectMethods = ReflectUtils.findMethodsAnnotatedWith(mapperType, Select.class);
			if(selectMethods != null && selectMethods.size() > 0) {
				if(selectMethods.size() > 1) {
					throw new RuntimeException("有多个@Select方法");
				}
				selectStatementId = selectMethods.get(0).getName();
			}
			
		}
		
		buildInsert(insertStatementId);
		buildDelete(deleteStatementId);
		buildUpdate(updateStatementId);
		buildSelect(selectStatementId);
		
	}
	
	private void buildInsert(String statementId) {
		//
		Integer fetchSize = null;
		Integer timeout = null;
		Class<?> parameterType = entityClass;
		
		///~~~~~~~~~~
		boolean flushCache = true;
		boolean useCache = false;
		boolean resultOrdered = false;
		KeyGenerator keyGenerator = null;
		String keyProperty = null;
		String keyColumn = null;
		
		String keyStatementId = entityClass.getName() + ".insert" + SelectKeyGenerator.SELECT_KEY_SUFFIX;
		if (configuration.hasKeyGenerator(keyStatementId)) {
			keyGenerator = configuration.getKeyGenerator(keyStatementId);
		} else {
			Id id = AnnotationUtils.findDeclaredAnnotation(Id.class, entityClass);
			keyGenerator = id.generatedKeys() ? new Jdbc3KeyGenerator() : new NoKeyGenerator();
			
			keyProperty = idField.getName();
			keyColumn = StringUtils.isBlank(id.column()) ? CaseFormatUtils.camelToUnderScore(idField.getName()) : id.column();
		}

		List<SqlNode> contents = new ArrayList<SqlNode>();
		contents.add(this.getInsertSql());
		SqlSource sqlSource = new DynamicSqlSource(configuration, new MixedSqlNode(contents));

		assistant.addMappedStatement(statementId, sqlSource, StatementType.PREPARED,
				SqlCommandType.INSERT, fetchSize, timeout, null, parameterType, null, null,
				ResultSetType.FORWARD_ONLY, flushCache, useCache, resultOrdered, keyGenerator,
				keyProperty, keyColumn, databaseId, lang);
	}
	
	private SqlNode getInsertSql() {
		List<SqlNode> contents = new ArrayList<SqlNode>();
		contents.add(new TextSqlNode("INSERT INTO " + tableName + " "));
		contents.add(getInsertColumns());
		contents.add(getInsertFileds());
		return new MixedSqlNode(contents);
	}
	
	private SqlNode getInsertFileds() {
		List<SqlNode> contents = new ArrayList<SqlNode>();
		for(Field field : columnFields){
			List<SqlNode> sqlNodes = new ArrayList<SqlNode>();

			if(Date.class.isAssignableFrom(field.getType()) 
					&& field.getAnnotation(Column.class) != null 
					&& field.getAnnotation(Column.class).sysdate() == true){
				sqlNodes.add(new TextSqlNode("now(),"));
			} else {
				sqlNodes.add(new TextSqlNode("#{" + field.getName() + "},"));
			}
			
			contents.add(new IfSqlNode(new MixedSqlNode(sqlNodes), getTestByField(field)));
		}
		
		return new TrimSqlNode(configuration, new MixedSqlNode(contents), " VALUES (", null, ")", ",");
	}

	private TrimSqlNode getInsertColumns(){
		List<SqlNode> contents = new ArrayList<SqlNode>();
		for(Field field : columnFields){
			List<SqlNode> sqlNodes = new ArrayList<SqlNode>();
			sqlNodes.add(new TextSqlNode(getColumnNameByField(field) + ","));
			
			contents.add(new IfSqlNode(new MixedSqlNode(sqlNodes), getTestByField(field)));
		}
		
		return new TrimSqlNode(configuration, new MixedSqlNode(contents), "(", null, ")", ",");
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//delete
	private void buildDelete(String statementId){
		Integer timeout = null;
		Class<?> parameterType = entityClass;
		
		//~~~~~~~~~~~~~~~~~~~~~~~
		boolean flushCache = true;
		boolean useCache = false;
		boolean resultOrdered = false;
		KeyGenerator keyGenerator = new NoKeyGenerator();
		
		SqlNode sqlNode = new TextSqlNode("DELETE FROM " + tableName + " WHERE " + getIdColumnName() + " = #{" + getIdFieldName() + "} " + getVersionSQL());
		
		SqlSource sqlSource = new DynamicSqlSource(configuration, sqlNode);
		
		assistant.addMappedStatement(statementId, sqlSource, StatementType.PREPARED,
				SqlCommandType.DELETE, null, timeout, null, parameterType, null, null, null,
				flushCache, useCache, resultOrdered, keyGenerator, null, null, databaseId, lang);
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//update
	private void buildUpdate(String statementId){
		Integer timeout = null;
		Class<?> parameterType = entityClass;
		
		//~~~~~~~~~~~~~
		boolean flushCache = true;
		boolean useCache = false;
		boolean resultOrdered = false;
		KeyGenerator keyGenerator = new NoKeyGenerator();
		
		List<SqlNode> contents = new ArrayList<SqlNode>();
		contents.add(this.getUpdateSql());
		
		SqlSource sqlSource = new DynamicSqlSource(configuration, new MixedSqlNode(contents));
		
		assistant.addMappedStatement(statementId, sqlSource, StatementType.PREPARED,
				SqlCommandType.UPDATE, null, timeout, null, parameterType, null, null, null,
				flushCache, useCache, resultOrdered, keyGenerator, null, null, databaseId, lang);
	}
	
	private SqlNode getUpdateSql(){
		List<SqlNode> contents = new ArrayList<SqlNode>();
		contents.add(new TextSqlNode("UPDATE " + tableName + " "));
		contents.add(getUpdateColumns());
		
		contents.add(new TextSqlNode(" WHERE " + getIdColumnName() + " = #{" + getIdFieldName() + "}" + getVersionSQL()));
		return new MixedSqlNode(contents);
	}
	
	private SqlNode getUpdateColumns(){
		List<SqlNode> contents = new ArrayList<SqlNode>();
		for(Field field : columnFields){
			List<SqlNode> sqlNodes = new ArrayList<SqlNode>();
			
			if(Date.class.isAssignableFrom(field.getType()) 
					&& field.getAnnotation(Column.class) != null 
					&& field.getAnnotation(Column.class).sysdate() == true){
				sqlNodes.add(new TextSqlNode(getColumnNameByField(field) + " = now(),"));
			} else {
				sqlNodes.add(new TextSqlNode(getColumnNameByField(field) + " = #{" + field.getName() + "},"));
			}
			
			contents.add(new IfSqlNode(new MixedSqlNode(sqlNodes), getTestByField(field)));
		}
		
		return new SetSqlNode(configuration, new MixedSqlNode(contents));
	}
	
	//~~~~~~~~~~~~~~~~~
	//get
	private void buildSelect(String statementId){
		Integer fetchSize  = null;
		Integer timeout = null;
		Class<?> resultType = entityClass;
		
		//~~~~~~~~~~~~~~~~~
		boolean flushCache = false;
		boolean useCache = true;
		boolean resultOrdered = false;
		KeyGenerator keyGenerator = new NoKeyGenerator();
		
		List<SqlNode> contents = new ArrayList<SqlNode>();
		contents.add(this.getGetSql());
		
		SqlSource sqlSource = new DynamicSqlSource(configuration, new MixedSqlNode(contents));
		
		assistant.addMappedStatement(statementId, sqlSource, StatementType.PREPARED,
				SqlCommandType.SELECT, fetchSize, timeout, null, null, null, resultType, null,
				flushCache, useCache, resultOrdered, keyGenerator, null, null, databaseId, lang);
	}
	
	private SqlNode getGetSql(){
		String sql = "SELECT " + getIdColumnName() + " AS " + getIdFieldName();
		
		for(Field field : columnFields){
			sql += "," + getColumnNameByField(field) + " AS " + field.getName();
		}
		
		sql += " FROM " + tableName + " WHERE " + getIdColumnName() + " = #{" + getIdFieldName() + "}";
		
		return new TextSqlNode(sql);
	}

}
