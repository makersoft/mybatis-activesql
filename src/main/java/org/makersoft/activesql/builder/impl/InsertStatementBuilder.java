/*
 * @(#)InsertStatementBuilder.java 2013年12月23日 下午23:33:33
 *
 * Copyright (c) 2011-2013 Makersoft.org all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 */
package org.makersoft.activesql.builder.impl;

import org.apache.ibatis.session.Configuration;
import org.makersoft.activesql.builder.GenericStatementBuilder;

/**
 */
public class InsertStatementBuilder extends GenericStatementBuilder{

	public InsertStatementBuilder(Configuration configuration, Class<?> clazz) {
		super(configuration, clazz);
	}

	@Override
	public void build() {
		
	}

}
