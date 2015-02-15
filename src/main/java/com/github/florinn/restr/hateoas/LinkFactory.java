package com.github.florinn.restr.hateoas;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.github.florinn.restr.core.Entity;

public class LinkFactory {
	
	/**
	 * @param fqBasePath fully qualified base path
	 * @param entity resource instance registered with {@code RestResourceDefinitionRegistry}
	 * @return representation of input resource instance using the default resource representation template class
	 */
	public static <T extends Entity<?>, U extends Link<T>> Link<T> getLink(String fqBasePath, T entity) 
			throws NoSuchMethodException, SecurityException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		RestResourceDefinition<?, U> rd = RestResourceDefinitionRegistry.getResourceDefinition(entity);
		
		Link<T> object = getLink(fqBasePath, entity, rd.getDefaultRepresentationClass());
		
		return object;
	}
	
	/**
	 * @param fqBasePath fully qualified base path
	 * @param entity resource instance registered with {@code RestResourceDefinitionRegistry}
	 * @param clazz resource representation template class
	 * @return representation of input resource instance using the specified resource representation template class
	 */
	public static <T extends Entity<?>, U extends Link<T>> Link<T> getLink(String fqBasePath, T entity, Class<U> clazz) 
			throws NoSuchMethodException, SecurityException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Constructor<U> cons = clazz.getConstructor(String.class, entity.getClass());

		U object = cons.newInstance(fqBasePath, entity);

		return object;
	}
}
