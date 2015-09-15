package org.pure4j.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class FieldHandle extends MemberHandle {

	public FieldHandle(Field f) {
		super();
		this.className = convertClassName(f.getDeclaringClass());
		this.name = f.getName();
		this.desc = null;
	}

	public FieldHandle(String className, String name) {
		this.name = name;
		this.className = className;
	}

	public Field hydrate(ClassLoader cl) {
		return hydrateField(this, cl);
	}

	public String getDeclaringClass() {
		return className;
	}

	public <T extends Annotation> T getAnnotation(ClassLoader cl, Class<T> c) {
		Field f = hydrate(cl);
		return f.getAnnotation(c);
	}
	
	@Override
	public Class<?> getDeclaringClass(ClassLoader cl) {
		return hydrateClass(className, cl);
	}

	@Override
	public java.lang.reflect.Type[] getGenericTypes(ClassLoader cl) {
		return new java.lang.reflect.Type[] { hydrate(cl).getGenericType() };
	}
	
	public Class<?>[] getRawTypes(ClassLoader cl) {
		return new Class<?>[] { hydrate(cl).getType()};
	}

	@Override
	public int getModifiers(ClassLoader cl) {
		return hydrate(cl).getModifiers();
	}
	
	
	
}
