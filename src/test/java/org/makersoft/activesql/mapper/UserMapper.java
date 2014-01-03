/*
 * @(#)UserMapper.java 2013年12月23日 下午23:33:33
 *
 * Copyright (c) 2011-2013 Makersoft.org all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 */
package org.makersoft.activesql.mapper;

import org.makersoft.activesql.annotations.Delete;
import org.makersoft.activesql.annotations.Insert;
import org.makersoft.activesql.annotations.Select;
import org.makersoft.activesql.annotations.Update;
import org.makersoft.activesql.model.User;

/**
 * Class description goes here.
 */
public interface UserMapper {

	@Insert
	int insertUser(User user);
	
	@Delete
	int delete(User user);
	
	@Update
	int update(User user);
	
	@Select
	User get(Long id);
}
