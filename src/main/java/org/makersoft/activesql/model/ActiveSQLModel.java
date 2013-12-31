/*
 * @(#)ActiveSQLModel.java 2013年12月27日 下午23:33:33
 *
 * Copyright (c) 2011-2013 Makersoft.org all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 */
package org.makersoft.activesql.model;

import java.io.Serializable;

import org.makersoft.activesql.persistence.MyBatisContext;

/**
 * Class description goes here.
 */
public abstract class ActiveSQLModel<PK, T> implements Serializable{

	private static final long serialVersionUID = -7720270093702372957L;
	
	// --- persist method ---
	public int create() {
		return MyBatisContext.insert(this);
	}
	
	public int update(){
		return MyBatisContext.update(this);
	}

	public int remove() {
		return MyBatisContext.delete(this);
	}
	
	public static <T> T load(Long id){
		return null;
	}
	
	public static <T> T get(Long id) {
		return null;
	}
}
