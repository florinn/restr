package com.github.florinn.restr.hateoas.sample;

import com.github.florinn.restr.core.Entity;
import com.github.florinn.restr.core.EntityRef;

public class Location extends Entity<String> {

	public static class GeoCoordinate {
		private double latitude;
		private double longitude;

		public GeoCoordinate() {}
		public GeoCoordinate(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}
		
		public double getLatitude() {
			return latitude;
		}
		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}
		public double getLongitude() {
			return longitude;
		}
		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}
	}

	private EntityRef<String, User> user;
	private GeoCoordinate geoCoordinate;
	
	public Location(
			String id,
			EntityRef<String, User> user,
			GeoCoordinate geoCoordinate) {
		this.id = id;
		this.user = user;
		this.geoCoordinate = geoCoordinate;
	}
	
	public EntityRef<String, User> getUser() {
		return user;
	}
	public void setUser(EntityRef<String, User> user) {
		this.user = user;
	}
	public GeoCoordinate getGeoCoordinate() {
		return geoCoordinate;
	}
	public void setGeoCoordinate(GeoCoordinate geoCoordinate) {
		this.geoCoordinate = geoCoordinate;
	}
}
