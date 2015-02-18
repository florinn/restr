package com.github.florinn.restr.hateoas.sample;

import com.github.florinn.restr.hateoas.Link;

@SuppressWarnings("serial")
public class CalendarRepresent extends Link<Calendar> {

	protected CalendarRepresent(String fqBasePath, Calendar entity) {
		super(fqBasePath, entity);
	}

}
