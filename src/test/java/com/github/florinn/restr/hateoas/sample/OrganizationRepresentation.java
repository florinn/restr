package com.github.florinn.restr.hateoas.sample;

import com.github.florinn.restr.hateoas.Link;

@SuppressWarnings("serial")
public class OrganizationRepresentation extends Link<Organization> {

	public OrganizationRepresentation(String fqBasePath, Organization org) {
		super(fqBasePath, org);
	}

}
