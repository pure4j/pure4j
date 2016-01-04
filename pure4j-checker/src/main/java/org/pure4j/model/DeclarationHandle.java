package org.pure4j.model;

import org.pure4j.model.impl.ClassHandle;

public interface DeclarationHandle extends CallHandle, AnnotatedElementHandle {

	public int getLine();
	
	public int getModifiers();
	
	public boolean isStatic();

	public boolean isFinal();

	public boolean isPublic();

	public boolean isPrivate();

	public boolean isProtected();
	
	public boolean isSynthetic();
	
	public ClassHandle getDeclaringClass();

}
