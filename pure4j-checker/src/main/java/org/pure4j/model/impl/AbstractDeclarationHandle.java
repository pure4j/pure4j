package org.pure4j.model.impl;

import java.lang.reflect.Modifier;

import org.pure4j.model.DeclarationHandle;

public abstract class AbstractDeclarationHandle extends AbstractMemberHandle implements DeclarationHandle {

	public AbstractDeclarationHandle(String className, String name, String desc, int line, int modifiers, ClassHandle ch) {
		super(className, name, desc, line);
		this.modifiers = modifiers;
		this.declaringClass = ch;
	}
 
	public ClassHandle getDeclaringClass() {
		return declaringClass;
	}

	protected int modifiers;
	protected ClassHandle declaringClass;

	public int getModifiers() {
		return modifiers;
	}
	
	public boolean isAbstract() {
		return Modifier.isAbstract(modifiers);
	}
	
	public boolean isStatic() {
		return Modifier.isStatic(modifiers);
	}

	public boolean isFinal() {
		return Modifier.isFinal(modifiers);
	}

	public boolean isPublic() {
		return Modifier.isPublic(modifiers);
	}

	public boolean isPrivate() {
		return Modifier.isPrivate(modifiers);
	}

	public boolean isProtected() {
		return Modifier.isProtected(modifiers);
	}
	
	public boolean isSynthetic() {
		return (getModifiers() & 4096) == 4096;
	}
	

}

