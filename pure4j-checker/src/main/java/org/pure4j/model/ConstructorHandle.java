package org.pure4j.model;

import java.lang.reflect.Constructor;

public class ConstructorHandle extends MemberHandle {

	public ConstructorHandle(Constructor<?> c) {
		super();
		this.className = convertClassName(c.getDeclaringClass());
		this.desc = Type.getConstructorDescriptor(c);
	    this.name = "<init>";
	}

	public ConstructorHandle(String className, String desc) {
	    this.className = className;
	    this.desc = desc;
	    this.name = "<init>";
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


}
