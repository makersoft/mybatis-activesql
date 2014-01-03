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
 * Active SQL basic model.
 */
public abstract class ActiveSQLModel implements Serializable {

	private static final long serialVersionUID = -7720270093702372957L;

	// --- persist method ---
	public boolean create() {
		return MyBatisContext.insert(this) == 1;
	}

	public boolean update() {
		return MyBatisContext.update(this) == 1;
	}

	public boolean remove() {
		return MyBatisContext.delete(this) == 1;
	}

//	public static <T> T load(Long id) {
//		throw new UnsupportedOperationException("Unimplement static method exception!!!");
//	}

	public static <T> T get(Long id) {
		throw new UnsupportedOperationException("Unimplement static method exception!!!");
	}
}
