package com.github.florinn.restr.hateoas.sample;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import com.github.florinn.restr.hateoas.Link;
import com.github.florinn.restr.hateoas.RestResourceDefinitionRegistry;

@SuppressWarnings("serial")
public class LocationRepresent extends Link<Location> {

	public LocationRepresent(String fqBasePath, Location location) {
		super(fqBasePath, location);
		
		Map<String, Object> links = new LinkedHashMap<String,Object>();
		
		URI userCalendarsUri = UriBuilder.fromPath(
				RestResourceDefinitionRegistry.getResourceDefinition(Calendar.class).getPathTemplate())
				.build(location.getUser().getId());
		Link<?> calendarLink = Link.from(fqBasePath, userCalendarsUri.getPath());
		links.put("calendars", calendarLink);
		
		this.put("meta", links);
	}

}
