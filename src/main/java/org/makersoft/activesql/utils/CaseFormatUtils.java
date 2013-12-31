/*
 * @(#)CaseFormatUtils.java 2013年12月24日 下午23:33:33
 *
 * Copyright (c) 2011-2013 Makersoft.org all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 */
package org.makersoft.activesql.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Case formats utils.
 */
public class CaseFormatUtils {
	
	/**
	 * camel case convert to under score case
	 */
	public static String camelToUnderScore(String camelCase){
		return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(camelCase), "_").toLowerCase();
	}
}
