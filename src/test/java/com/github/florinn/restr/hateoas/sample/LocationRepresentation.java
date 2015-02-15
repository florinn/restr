package com.github.florinn.restr.hateoas.sample;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import com.github.florinn.restr.hateoas.Link;
import com.github.florinn.restr.hateoas.RestResourceDefinitionRegistry;

@SuppressWarnings("serial")
public class LocationRepresentation extends Link<Location> {

	public LocationRepresentation(String fqBasePath, Location location) {
		super(fqBasePath, location);
		
		Map<String, Object> links = new LinkedHashMap<String,Object>();
		
		URI dataUri = UriBuilder.fromPath(
				RestResourceDefinitionRegistry.getResourceDefinition(Organization.class).getPathTemplate())
				.build(location.getOrg().getId());
		Link<?> commands = Link.from(fqBasePath, dataUri.getPath());
		links.put("data", commands);
		
		this.put("meta", links);
	}

}
