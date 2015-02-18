package com.github.florinn.restr.hateoas.sample;

import com.github.florinn.restr.hateoas.Link;

@SuppressWarnings("serial")
public class UserRepresent extends Link<User> {

	public UserRepresent(String fqBasePath, User user) {
		super(fqBasePath, user);
	}

}
