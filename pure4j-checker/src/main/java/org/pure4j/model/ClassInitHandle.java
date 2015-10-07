package org.pure4j.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

public class ClassInitHandle extends MemberHandle implements ImplementationHandle {

	public ClassInitHandle(String className, String desc, int line) {
		super(className, "<clinit>", desc, line);
	}

	public AccessibleObject hydrate(ClassLoader cl) {
	    return null;
	}
	
	public Class<?> hydrateClass(ClassLoader cl) {
	    return hydrateClass(className, cl);
	}

	public String getDeclaringClass() {
		return className;
	}

	public <T extends Annotation> T getAnnotation(ClassLoader cl, Class<T> c) {
		Class<?> con = hydrateClass(cl);
		return con.getAnnotation(c);
	}
	
	@Override
	public Class<?> getDeclaringClass(ClassLoader cl) {
		return hydrateClass(cl);
	}

	@Override
	public java.lang.reflect.Type[] getGenericTypes(ClassLoader cl) {
		return new java.lang.reflect.Type[] {};
	}

	public Class<?>[] getRawTypes(ClassLoader cl) {
		return new Class[] {};
	}
	
	@Override
	public int getModifiers(ClassLoader cl) {
		return hydrateClass(cl).getModifiers();
	}
}
