package com.github.florinn.restr.hateoas;

import java.net.URI;

import com.github.florinn.restr.core.Entity;

public abstract class RestResourceDefinition<T extends Entity<?>, U extends Link<T>> {
	private final Class<T> resourceClass;
	private final Class<U> defaultRepresentationClass;
	private final String pathTemplate;
	
	public RestResourceDefinition(
			Class<T> resourceClass,
			Class<U> defaultRepresentationClass,
			String pathTemplate) {
		this.resourceClass = resourceClass;
		this.defaultRepresentationClass = defaultRepresentationClass;
		this.pathTemplate = pathTemplate;
	}

	public abstract URI getPath(T entity);
	
	public Class<T> getResourceClass() {
		return resourceClass;
	}
	public Class<U> getDefaultRepresentationClass() {
		return defaultRepresentationClass;
	}
	public String getPathTemplate() {
		return pathTemplate;
	}
}
