package com.github.florinn.restr.hateoas.sample;

import com.github.florinn.restr.core.Entity;
import com.github.florinn.restr.core.EntityRef;

public class Calendar extends Entity<String> {

	private EntityRef<String, User> user;
	private String description;

	
	public Calendar(EntityRef<String, User> user, String description) {
		super();
		this.user = user;
		this.description = description;
	}

	public EntityRef<String, User> getUser() {
		return user;
	}

	public void setUser(EntityRef<String, User> user) {
		this.user = user;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
