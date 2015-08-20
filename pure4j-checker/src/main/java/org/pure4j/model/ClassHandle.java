package org.pure4j.model;

import java.lang.reflect.AnnotatedElement;

public class ClassHandle extends AbstractHandle<Class<?>> implements AnnotatedElementHandle<Class<?>> {

	public ClassHandle(Class<?> c) {
		this.className = convertClassName(c);
	}

	public ClassHandle(String className) {
		this.className = className;
	}

	protected String className;

	public Class<?> hydrate(ClassLoader cl) {
		return hydrateClass(className, cl);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassHandle other = (ClassHandle) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		return true;
	}

	public int compareTo(AnnotatedElementHandle<? extends AnnotatedElement> o) {
		if (o instanceof ClassHandle) {
			ClassHandle ch = (ClassHandle) o;
			return this.className.compareTo(ch.className);
		} else {
			return -1;
		}
	}

	public String getDeclaringClass() {
		return className;
	}

}
