package com.github.florinn.restr.hateoas;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.florinn.restr.json.RawMapSerializer;
import com.github.florinn.restr.core.Entity;
import com.github.florinn.restr.core.EntityRef;

@SuppressWarnings("serial")
@JsonSerialize(using = RawMapSerializer.class)
public class Link<T extends Entity<?>> extends LinkedHashMap<String, Object> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Link.class);

	public static final String PATH_SEPARATOR = "/";
	public static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.disable(Feature.WRITE_DATES_AS_TIMESTAMPS);
	}

	private Link(String href) {
		putHref(href);
	}
	public Link(String fqBasePath, T entity) {
		fqBasePath = sanitizeFullyQualifiedContextPath(fqBasePath);
		String href = createHref(fqBasePath, entity);
		putHref(href);
		this.putEntityProps(fqBasePath, entity);
	}

	public static Link<?> from(String fqBasePath, String subPath) {
		fqBasePath = sanitizeFullyQualifiedContextPath(fqBasePath);
		if (!subPath.startsWith(PATH_SEPARATOR)) {
			subPath = PATH_SEPARATOR + subPath;
		}
		String href = fqBasePath + subPath;
		Link<?> link = new Link<Entity<?>>(href);
		return link;
	}

	protected static String sanitizeFullyQualifiedContextPath(String fqBasePath) {
		if (fqBasePath.endsWith(PATH_SEPARATOR)) {
			return fqBasePath.substring(0, fqBasePath.length() - 1);
		}
		return fqBasePath;
	}

	protected String createHref(String fqBasePath, Entity<?> entity) {
		String result = null;
		URI resourcePath = RestResourceDefinitionRegistry.getPath(entity);
		if(resourcePath != null) {
			fqBasePath = sanitizeFullyQualifiedContextPath(fqBasePath);
			StringBuilder sb = new StringBuilder(fqBasePath);
			sb.append(resourcePath).append(PATH_SEPARATOR).append(entity.getId());
			result = sb.toString();
		}
		return result;
	}

	private void putHref(String href) {
		if(href != null)
			this.put("href", href);
	}
	public String getHref() {
		return (String) get("href");
	}

	@JsonIgnore
	public String asJSON() throws JsonGenerationException,
			JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String result = mapper.writeValueAsString(this);
		return result;
	}

	private void putEntityProps(String fqBasePath, Entity<?> entity) {
		@SuppressWarnings("unchecked")
		Map<String, Object> propsMap = Collections.checkedMap(
				mapper.convertValue(entity, Map.class), String.class, Object.class);

		try {
			BeanInfo entityBeanInfo = Introspector.getBeanInfo(entity.getClass());

			for (PropertyDescriptor pd : entityBeanInfo.getPropertyDescriptors()) {
				Class<?> propClazz = pd.getPropertyType();

				try {
					if (pd.getReadMethod() != null
							&& !"class".equals(pd.getName())) {

						if (EntityRef.class.isAssignableFrom(propClazz)) {

							Object propValue = pd.getReadMethod().invoke(entity);

							if (propValue != null) {
								@SuppressWarnings("unchecked")
								EntityRef<? extends Entity<?>, ?> entityDBRef = (EntityRef<? extends Entity<?>, ?>) propValue;

								URI baseUri = RestResourceDefinitionRegistry.getPath(entity);
								StringBuilder sb = new StringBuilder(baseUri.getPath());
								sb.append(PATH_SEPARATOR).append(entityDBRef.getId());

								Link<?> link = Link.from(fqBasePath, sb.toString());
								propsMap.put(pd.getName(), link);
							}
						} 
						else if (Entity.class.isAssignableFrom(propClazz)) {

							Object propValue = pd.getReadMethod().invoke(entity);

							if (propValue != null) {
								Link<?> link = LinkFactory.getLink(fqBasePath, (Entity<?>) propValue);
								propsMap.put(pd.getName(), link);
							}
						}
					}

				} catch (Exception e) {
					LOGGER.error("put entity props parsing error", e);
				}
			}

		} catch (IntrospectionException e) {
			LOGGER.error("put entity props introspection error", e);
		}

		this.putAll(propsMap);
	}

}