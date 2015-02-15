package com.github.florinn.restr.core;

public class EntityRef<T, U extends Entity<T>> {
	private T id;
	private String classRef;

	public static <T, U extends Entity<T>> EntityRef<T, U> from(U entity) {
		EntityRef<T, U> entityRef = new EntityRef<T, U>();
		entityRef.setId(entity.getId());
		entityRef.setClassRef(entity.getClass().getName());
		return entityRef;
	}
	
	public Class<U> toClazz() throws ClassNotFoundException {
		@SuppressWarnings("unchecked")
		Class<U> forName = (Class<U>) Class.forName(classRef);
		return forName;
	}
	
	public T getId() {
		return id;
	}
	public void setId(T id) {
		this.id = id;
	}
	public String getClassRef() {
		return classRef;
	}
	public void setClassRef(String classRef) {
		this.classRef = classRef;
	}
}
