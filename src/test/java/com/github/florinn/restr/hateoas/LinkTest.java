package com.github.florinn.restr.hateoas;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.junit.Test;

import com.github.florinn.restr.hateoas.sample.User;
import com.github.florinn.restr.hateoas.sample.UserRepresent;

public class LinkTest {

	String fqBasePath = "http://localhost";
	String usersPath = "/users";
	
	@Test
	public void hrefResourceRepresentationFromEntity() throws Exception {

		RestResourceDefinition<User, UserRepresent> userResourceDefinition = 
				new RestResourceDefinition<User, UserRepresent>(User.class, UserRepresent.class, usersPath) {

			@Override
			public URI getPath(User user) {
				URI path = UriBuilder.fromPath(getPathTemplate()).build();
				return path;
			}

		};
		RestResourceDefinitionRegistry.registerResourceDefinition(userResourceDefinition);


		User user = new User("jdoe", "John Doe");

		Link<User> userHrefLink = Link.from(fqBasePath, user);


		Link<User> userRepresentation = new UserRepresent(fqBasePath, user);
		assertThat(userHrefLink.getHref()).isEqualTo(userRepresentation.getHref());
		assertThat(userHrefLink.asJSON()).isEqualTo(String.format("{\"href\":\"%s\"}", userRepresentation.getHref()));
	}
	
	@Test
	public void hrefResourceRepresentationFromSubpath() throws Exception {
		
		RestResourceDefinition<User, UserRepresent> userResourceDefinition = 
				new RestResourceDefinition<User, UserRepresent>(User.class, UserRepresent.class, usersPath) {
			
			@Override
			public URI getPath(User user) {
				URI path = UriBuilder.fromPath(getPathTemplate()).build();
				return path;
			}
			
		};
		RestResourceDefinitionRegistry.registerResourceDefinition(userResourceDefinition);
		
		
		User user = new User("jdoe", "John Doe");
		
		URI usersUri = UriBuilder.fromPath(
				RestResourceDefinitionRegistry.getResourceDefinition(User.class).getPathTemplate())
				.build();
		Link<?> userHrefLink = Link.from(fqBasePath, String.format("%s/%s", usersUri.getPath(), user.getId()));
		
		
		Link<User> userRepresentation = new UserRepresent(fqBasePath, user);
		assertThat(userHrefLink.getHref()).isEqualTo(userRepresentation.getHref());
		assertThat(userHrefLink.asJSON()).isEqualTo(String.format("{\"href\":\"%s\"}", userRepresentation.getHref()));
	}
	
}
