package com.github.florinn.restr.hateoas.sample;

import com.github.florinn.restr.core.Entity;

public class Organization extends Entity<String> {

	private String name;
	
	public Organization(String orgId, String orgName) {
		this.id = orgId;
		this.name = orgName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
