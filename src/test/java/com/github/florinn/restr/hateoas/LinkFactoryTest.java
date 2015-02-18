package com.github.florinn.restr.hateoas;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.junit.Test;

import com.github.florinn.restr.core.Entity;
import com.github.florinn.restr.core.EntityRef;
import com.github.florinn.restr.hateoas.sample.Calendar;
import com.github.florinn.restr.hateoas.sample.CalendarRepresent;
import com.github.florinn.restr.hateoas.sample.Location;
import com.github.florinn.restr.hateoas.sample.LocationRepresent;
import com.github.florinn.restr.hateoas.sample.User;
import com.github.florinn.restr.hateoas.sample.UserRepresent;

public class LinkFactoryTest {

	String fqBasePath = "http://localhost";
	String usersPath = "/users";
	String locationsPath = usersPath + "/{user_id}/locations";
	String calendarsPath = usersPath + "/{user_id}/calendars";
	
	@Test
	public void simpleResourceRepresentation() throws Exception {

		RestResourceDefinition<User, UserRepresent> userResourceDefinition = 
				new RestResourceDefinition<User, UserRepresent>(User.class, UserRepresent.class, usersPath) {

			@Override
			public URI getPath(Entity<?> user) {
				URI path = UriBuilder.fromPath(getPathTemplate()).build();
				return path;
			}

		};
		RestResourceDefinitionRegistry.registerResourceDefinition(userResourceDefinition);


		User user = new User("jdoe", "John Doe");

		Link<User> userLink = LinkFactory.getLink(fqBasePath, user);


		Link<User> userRepresentation = new UserRepresent(fqBasePath, user);
		assertThat(userLink).isEqualTo(userRepresentation);
		assertThat(userLink.asJSON()).isEqualTo(userRepresentation.asJSON());
	}

	@Test
	public void hierarchicalResourceRepresentation() throws Exception {

		RestResourceDefinition<User, UserRepresent> userResourceDefinition = 
				new RestResourceDefinition<User, UserRepresent>(User.class, UserRepresent.class, usersPath) {

			@Override
			public URI getPath(Entity<?> user) {
				URI path = UriBuilder.fromPath(getPathTemplate()).build();
				return path;
			}

		};
		RestResourceDefinitionRegistry.registerResourceDefinition(userResourceDefinition);

		RestResourceDefinition<Calendar, CalendarRepresent> calendarResourceDefinition = 
				new RestResourceDefinition<Calendar, CalendarRepresent>(Calendar.class, CalendarRepresent.class, calendarsPath) {
			
			@Override
			public URI getPath(Entity<?> entity) {
				Calendar calendar = (Calendar) entity;
				URI path = UriBuilder.fromPath(getPathTemplate()).build(calendar.getUser().getId());
				return path;
			}
			
		};
		RestResourceDefinitionRegistry.registerResourceDefinition(calendarResourceDefinition);
		
		RestResourceDefinition<Location, LocationRepresent> locationResourceDefinition = 
				new RestResourceDefinition<Location, LocationRepresent>(Location.class, LocationRepresent.class, locationsPath) {

			@Override
			public URI getPath(Entity<?> entity) {
				Location location = (Location) entity;
				URI path = UriBuilder.fromPath(getPathTemplate()).build(location.getUser().getId());
				return path;
			}

		};
		RestResourceDefinitionRegistry.registerResourceDefinition(locationResourceDefinition);


		User user = new User("jdoe", "John Doe");
		EntityRef<String, User> userRef = EntityRef.from(user);
		Location.GeoCoordinate geoCoordinate = new Location.GeoCoordinate(123456, 654321);
		Location location = new Location("xyz", userRef, geoCoordinate);

		Link<Location> locationLink = LinkFactory.getLink(fqBasePath, location);


		Link<Location> locationRepresentation = new LocationRepresent(fqBasePath, location);
		assertThat(locationLink).isEqualTo(locationRepresentation);
		assertThat(locationLink.asJSON()).isEqualTo(locationRepresentation.asJSON());
	}

	@Test
	public void resourceRepresentationWithEmbeddedJSON() throws Exception {

		RestResourceDefinition<User, UserRepresent> userResourceDefinition = 
				new RestResourceDefinition<User, UserRepresent>(User.class, UserRepresent.class, usersPath) {

			@Override
			public URI getPath(Entity<?> user) {
				URI path = UriBuilder.fromPath(getPathTemplate()).build();
				return path;
			}

		};
		RestResourceDefinitionRegistry.registerResourceDefinition(userResourceDefinition);


		User user = new User("jdoe", "{ \"title\": \"Mr\", \"name\": \"John Doe\" }");

		Link<User> userLink = LinkFactory.getLink(fqBasePath, user);


		Link<User> userRepresentation = new UserRepresent(fqBasePath, user);
		assertThat(userLink).isEqualTo(userRepresentation);
		assertThat(userLink.asJSON()).isEqualTo(userRepresentation.asJSON());
	}

}
