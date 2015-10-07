package org.pure4j.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

public class ConstructorHandle extends MemberHandle {

	public ConstructorHandle(Constructor<?> c) {
		super(convertClassName(c.getDeclaringClass()), "<init>", Type.getConstructorDescriptor(c), 0);
	}
	

	public ConstructorHandle(String className, String desc, int line) {
		super(className, "<init>", desc, line);
	}

	public Constructor<?> hydrate(ClassLoader cl) {
	    return hydrateConstructor(this, cl);
	}
	
	public Class<?> hydrateClass(ClassLoader cl) {
	    return hydrateClass(className, cl);
	}

	public String getDeclaringClass() {
		return className;
	}

	public <T extends Annotation> T getAnnotation(ClassLoader cl, Class<T> c) {
		Constructor<?> con = hydrate(cl);
		return con.getAnnotation(c);
	}
	
	@Override
	public Class<?> getDeclaringClass(ClassLoader cl) {
		return hydrateClass(cl);
	}

	@Override
	public java.lang.reflect.Type[] getGenericTypes(ClassLoader cl) {
		return hydrate(cl).getGenericParameterTypes();
	}

	public Class<?>[] getRawTypes(ClassLoader cl) {
		return hydrate(cl).getParameterTypes();
	}
	
	@Override
	public int getModifiers(ClassLoader cl) {
		return hydrate(cl).getModifiers();
	}
}
