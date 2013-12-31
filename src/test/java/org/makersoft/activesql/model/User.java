/*
 * @(#)Users.java 2013年12月23日 下午23:33:33
 *
 * Copyright (c) 2011-2013 Makersoft.org all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 */
package org.makersoft.activesql.model;

import java.io.Serializable;
import java.util.Date;

import org.makersoft.activesql.annotations.Column;
import org.makersoft.activesql.annotations.Entity;
import org.makersoft.activesql.annotations.GeneratedValue;
import org.makersoft.activesql.annotations.GenerationType;
import org.makersoft.activesql.annotations.Id;
import org.makersoft.activesql.annotations.Table;
import org.makersoft.activesql.annotations.Transient;
import org.makersoft.activesql.annotations.Version;
import org.makersoft.activesql.mapper.UserMapper;

/**
 * Class description goes here.
 */
@Entity(mapper = UserMapper.class)
@Table(name = "t_user")
public class User implements Serializable{

	private static final long serialVersionUID = 6275980778279891698L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "password", test ="password != null and password != ''")
	private String password;

	@Column(name = "birth_day")
	private Date birthDay;
	
	@Transient
	private Integer age;
	
	@Column(name = "created_at", sysdate = true)
	private Date createdAt;
	
	@Version
	@Column(name = "updated_at", sysdate = true)
	private Date updatedAt;
	
	//~~~~~~~~~~~
	public User(){
		
	}
	
	public User(Long id){
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	
}
