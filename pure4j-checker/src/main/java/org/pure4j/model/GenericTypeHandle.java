package org.pure4j.model;

public interface GenericTypeHandle {

	boolean isAssignableFrom(GenericTypeHandle handle);
	
	boolean isArray();
	
	boolean isEnum();
}
