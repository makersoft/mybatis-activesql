/*
 * @(#)AnnotationUtils.java 2013年12月24日 下午23:33:33
 *
 * Copyright (c) 2011-2013 Makersoft.org all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 */
package org.makersoft.activesql.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Class description goes here.
 */
public class AnnotationUtils {

	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T findDeclaredAnnotation(Class<T> annoClazz, Class<?> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			Annotation[] annos = field.getDeclaredAnnotations();
			for(Annotation anno : annos){
				if(annoClazz.isAssignableFrom(anno.getClass())){
					return (T) anno;
				}
			}
		}
		
		return null;
	}
	
	public static <T> Field findDeclaredFieldWithAnnoation(Class<T> annoClazz, Class<?> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			Annotation[] annos = field.getDeclaredAnnotations();
			for(Annotation anno : annos){
				if(annoClazz.isAssignableFrom(anno.getClass())){
					return field;
				}
			}
		}
		
		return null;
	}
}
