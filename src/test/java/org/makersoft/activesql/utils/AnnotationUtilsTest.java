/*
 * @(#)AnnotationUtilsTest.java 2013年12月31日 下午23:33:33
 *
 * Copyright (c) 2011-2013 Makersoft.org all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 */
package org.makersoft.activesql.utils;

import static org.junit.Assert.*;
import org.junit.Test;
import org.makersoft.activesql.annotations.Column;
import org.makersoft.activesql.annotations.Entity;
import org.makersoft.activesql.annotations.Id;

public class AnnotationUtilsTest {
	@Entity
	public static class Test1 {

		@Id(column = "id")
		private Long id;
		
		@Column
		private String name;
		
		@Column
		private String passWd;
		
		@Column
		private Integer age;
	}

	@Test
	public void testFindDeclaredAnnotation() {
		Id id = AnnotationUtils.findDeclaredAnnotation(Id.class, Test1.class);
		assertNotNull(id);
		
		assertEquals("id", id.column());
	}
}
