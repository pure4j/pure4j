package org.pure4j.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class MethodHandle extends MemberHandle {

	public MethodHandle(Method m) {
		super(convertClassName(m.getDeclaringClass()), m.getName(), Type.getMethodDescriptor(m), 0);
	}

	public MethodHandle(String className, String name, String desc, int line) {
	    super(className, name, desc, line);
	}

	public Method hydrate(ClassLoader cl) {
	    return hydrateMethod(this, cl);
	}
	
	public Class<?> hydrateClass(ClassLoader cl) {
	    return hydrateClass(className, cl);
	}

	public String getDeclaringClass() {
		return className;
	}

	public boolean overrides(MethodHandle another) {
		return (this.name.equals(another.getName())) && (this.getDesc().equals(another.getDesc()));
	}

	public <T extends Annotation> T getAnnotation(ClassLoader cl, Class<T> c) {
		Method m = hydrate(cl);
		return m.getAnnotation(c);
	}
	
	@Override
	public Class<?> getDeclaringClass(ClassLoader cl) {
		return hydrateClass(className, cl);
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
