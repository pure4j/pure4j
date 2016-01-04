package org.pure4j.model.impl;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import org.pure4j.model.AnnotatedElementHandle;
import org.pure4j.model.GenericTypeHandle;
import org.pure4j.model.Handle;

public class ClassHandle extends AbstractAnnotatedHandle implements AnnotatedElementHandle, GenericTypeHandle {

	public ClassHandle(String className, String superclass, String[] interfaces, int modifiers, Classification c) {
		this.className = className;
		this.superclassName = superclass;
		this.interfaces = interfaces;
		this.modifiers  = modifiers;
		this.c = c;
	}
	
	enum Classification { CLASS, ENUM, PRIMITIVE, THROWABLE }

	protected String className;
	protected int modifiers;
	protected Classification c;
	protected String superclassName;
	protected String[] interfaces;

	public String getClassName() {
		return className;
	}

	@Override
	public String toString() {
		return "ClassHandle [className=" + className + "]";
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

	public int compareTo(Handle o) {
		if (o instanceof ClassHandle) {
			ClassHandle ch = (ClassHandle) o;
			return this.className.compareTo(ch.className);
		} else {
			return this.getClass().getName().compareTo(o.getClass().getName());
		}
	}

	public ClassHandle getDeclaringClass() {
		return this;
	}

	public boolean isInterface() {
		return Modifier.isInterface(modifiers);
	}

	public boolean isEnum() {
		return c == Classification.ENUM;
	}

	public boolean isConcrete() {
		return !Modifier.isAbstract(modifiers);
	}

	public boolean isFinal() {
		return Modifier.isFinal(modifiers);
	}

	List<FieldDeclarationHandle> fields = new LinkedList<FieldDeclarationHandle>();
	
	public List<FieldDeclarationHandle> getDeclaredFields() {
		return fields;
	}

	public String getSuperclass() {
		return superclassName;
	}

	public boolean isPrimitive() {
		return c == Classification.PRIMITIVE;
	}

	public boolean isObject() {
		return getClassName().equals(Object.class.getName());
	}

	public String[] getInterfaces() {
		return interfaces;
	}

	List<ConstructorDeclarationHandle> constructors = new LinkedList<ConstructorDeclarationHandle>();
	
	public List<ConstructorDeclarationHandle> getDeclaredConstructors() {
		return constructors;
	}
	
	public boolean isAnonymousInner() {
		int dollar = getClassName().lastIndexOf("$");
		if (dollar > -1) {
			String number = getClassName().substring(dollar+1);
			if (number.matches("[0-9]+")) {
				return true;
			}
		}
		
		return false;
	}


	public boolean isArray() {
		return false;
	}

	List<MethodDeclarationHandle> methods = new LinkedList<MethodDeclarationHandle>(); 
	
	public List<MethodDeclarationHandle> getDeclaredMethods() {
		return methods;
	}

	public boolean isThrowable() {
		return c == Classification.THROWABLE;
	}

	public String getDescriptor() {
		return className.replace('.', '/');
	}

	@Override
	public boolean isAssignableFrom(GenericTypeHandle handle) {
		// TODO Auto-generated method stub
		return false;
	}
}
