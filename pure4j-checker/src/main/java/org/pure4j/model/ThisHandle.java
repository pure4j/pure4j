package org.pure4j.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;

/**
 * Handle for the use of "this" in a method.
 * 
 * @author robmoffat
 *
 */
public class ThisHandle extends MemberHandle {

	public ThisHandle(String className) {
		super();
		this.className = className;
		this.desc = "";
		this.name = "<this>";
	}

	@Override
	public String getDeclaringClass() {
		return className;
	}

	@Override
	public int compareTo(AnnotatedElementHandle<? extends AnnotatedElement> o) {
		return this.className.compareTo(o.getDeclaringClass());
	}

	@Override
	public AccessibleObject hydrate(ClassLoader cl) {
		throw new UnsupportedOperationException("Can't hydrate <this>");
	}
	
	public <T extends Annotation> T getAnnotation(ClassLoader cl, Class<T> c) {
		return null;
	}
	
	@Override
	public Class<?> getDeclaringClass(ClassLoader cl) {
		return hydrateClass(className, cl);
	}
	
	@Override
	public java.lang.reflect.Type[] getGenericTypes(ClassLoader cl) {
		throw new UnsupportedOperationException("Can't hydrate <this>");
	}
}
