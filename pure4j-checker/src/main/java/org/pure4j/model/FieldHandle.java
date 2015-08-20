package org.pure4j.model;

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

}
