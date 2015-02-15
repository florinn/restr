package com.github.florinn.restr.hateoas;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.junit.Test;

import com.github.florinn.restr.core.EntityRef;
import com.github.florinn.restr.hateoas.sample.Location;
import com.github.florinn.restr.hateoas.sample.LocationRepresentation;
import com.github.florinn.restr.hateoas.sample.Organization;
import com.github.florinn.restr.hateoas.sample.OrganizationRepresentation;

public class LinkFactoryTest {

	String fqBasePath = "http://localhost";
	String orgPath = "/orgs";
	String locationPath = "/orgs/{org_id}/locations";
	
	@Test
	public void simpleResourceRepresentation() throws Exception {

		RestResourceDefinition<Organization, OrganizationRepresentation> organizationResourceDefinition = 
				new RestResourceDefinition<Organization, OrganizationRepresentation>(Organization.class, OrganizationRepresentation.class, orgPath) {

			@Override
			public URI getPath(Organization org) {
				URI path = UriBuilder.fromPath(getPathTemplate()).build();
				return path;
			}

		};
		RestResourceDefinitionRegistry.registerResourceDefinition(organizationResourceDefinition);


		Organization organization = new Organization("abc", "A B C");

		Link<Organization> organizationLink = LinkFactory.getLink(fqBasePath, organization);


		Link<Organization> organizationRepresentation = new OrganizationRepresentation(fqBasePath, organization);
		assertThat(organizationLink).isEqualTo(organizationRepresentation);
		assertThat(organizationLink.asJSON()).isEqualTo(organizationRepresentation.asJSON());
	}

	@Test
	public void hierarchicalResourceRepresentation() throws Exception {

		RestResourceDefinition<Organization, OrganizationRepresentation> organizationResourceDefinition = 
				new RestResourceDefinition<Organization, OrganizationRepresentation>(Organization.class, OrganizationRepresentation.class, orgPath) {

			@Override
			public URI getPath(Organization org) {
				URI path = UriBuilder.fromPath(getPathTemplate()).build();
				return path;
			}

		};
		RestResourceDefinitionRegistry.registerResourceDefinition(organizationResourceDefinition);

		RestResourceDefinition<Location, LocationRepresentation> locationResourceDefinition = 
				new RestResourceDefinition<Location, LocationRepresentation>(Location.class, LocationRepresentation.class, locationPath) {

			@Override
			public URI getPath(Location location) {
				URI path = UriBuilder.fromPath(getPathTemplate()).build(location.getOrg().getId());
				return path;
			}

		};
		RestResourceDefinitionRegistry.registerResourceDefinition(locationResourceDefinition);


		Organization organization = new Organization("abc", "A B C");
		EntityRef<String, Organization> orgRef = EntityRef.from(organization);
		Location.GeoCoordinate geoCoordinate = new Location.GeoCoordinate(123456, 654321);
		Location location = new Location("xyz", orgRef, geoCoordinate);

		Link<Location> locationLink = LinkFactory.getLink(fqBasePath, location);


		Link<Location> locationRepresentation = new LocationRepresentation(fqBasePath, location);
		assertThat(locationLink).isEqualTo(locationRepresentation);
		assertThat(locationLink.asJSON()).isEqualTo(locationRepresentation.asJSON());
	}

	@Test
	public void resourceRepresentationWithEmbeddedJSON() throws Exception {

		RestResourceDefinition<Organization, OrganizationRepresentation> organizationResourceDefinition = 
				new RestResourceDefinition<Organization, OrganizationRepresentation>(Organization.class, OrganizationRepresentation.class, orgPath) {

			@Override
			public URI getPath(Organization org) {
				URI path = UriBuilder.fromPath(getPathTemplate()).build();
				return path;
			}

		};
		RestResourceDefinitionRegistry.registerResourceDefinition(organizationResourceDefinition);


		Organization organization = new Organization("abc", "{ \"title\": \"A B C\", \"symbol\": \"a-b-c\" }");

		Link<Organization> organizationLink = LinkFactory.getLink(fqBasePath, organization);


		Link<Organization> organizationRepresentation = new OrganizationRepresentation(fqBasePath, organization);
		assertThat(organizationLink).isEqualTo(organizationRepresentation);
		assertThat(organizationLink.asJSON()).isEqualTo(organizationRepresentation.asJSON());
	}

}
