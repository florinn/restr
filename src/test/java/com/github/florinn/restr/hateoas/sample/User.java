package com.github.florinn.restr.hateoas.sample;

import com.github.florinn.restr.core.Entity;

public class User extends Entity<String> {

	private String name;
	
	public User(String userId, String userName) {
		this.id = userId;
		this.name = userName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
