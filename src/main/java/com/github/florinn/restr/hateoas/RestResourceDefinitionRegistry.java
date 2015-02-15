package com.github.florinn.restr.hateoas;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.florinn.restr.core.Entity;

public class RestResourceDefinitionRegistry {
	private static Map<Class<? extends Entity<?>>, RestResourceDefinition<? extends Entity<?>, ? extends Link<?>>> registry = 
			new LinkedHashMap<Class<? extends Entity<?>>, RestResourceDefinition<? extends Entity<?>, ? extends Link<?>>>();
	
	public static <T extends Entity<?>, U extends Link<T>> void registerResourceDefinition(RestResourceDefinition<T, U> resourceDefinition) {
		registry.put(resourceDefinition.getResourceClass(), resourceDefinition);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Entity<?>, U extends Link<T>> RestResourceDefinition<T, U> getResourceDefinition(Class<T> resourceClazz) {
		return (RestResourceDefinition<T, U>) registry.get(resourceClazz);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Entity<?>, U extends Link<T>> RestResourceDefinition<T, U> getResourceDefinition(T entity) {
		return getResourceDefinition((Class<T>)entity.getClass());
	}
		
	public static <T extends Entity<?>, U extends Link<T>> URI getPath(T entity) {
		URI result = null;
		RestResourceDefinition<T, U> resourceDefinition = getResourceDefinition(entity);
		if(resourceDefinition != null) {
			result = resourceDefinition.getPath(entity);
		}
		return result;
	}
	
	public static <T extends Entity<?>> void removeResourceDefinition(Class<T> resourceClazz) {
		registry.remove(resourceClazz);
	}	
}
