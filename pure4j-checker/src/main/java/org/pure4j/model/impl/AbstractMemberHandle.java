package org.pure4j.model.impl;

import org.pure4j.model.Handle;


/**
 * Handle to a class member (method or field).
 * Also contains static utility functions for converting from string name of class back into
 * Class object, and doing the same with methods and fields.
 * 
 * @author moffatr
 * 
 */
public abstract class AbstractMemberHandle extends AbstractAnnotatedHandle {

	protected String className;
	protected String name;
	protected String desc;
	protected int line;

	public AbstractMemberHandle(String className, String name, String desc, int line) {
		super();
		this.className = className;
		this.name = name;
		this.desc = desc;
		this.line = line;
	}

	public String getClassName() {
		return className;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public int getLine() {
		return line;
	}

	@Override
	public String toString() {
		return className + "." + name + (desc == null ? "" : desc)+(line != 0 ? " @line="+line : "");
	}

	public int compareTo(Handle oo) {
	    if (oo instanceof AbstractMemberHandle) {
	    	AbstractMemberHandle o = (AbstractMemberHandle) oo;
	    	return (className+desc+name).compareTo(o.className+o.desc+o.name);
	    } else {
			return this.getClass().getName().compareTo(oo.getClass().getName());
	    }
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((className == null) ? 0 : className.hashCode());
	    result = prime * result + ((desc == null) ? 0 : desc.hashCode());
	    result = prime * result + ((name == null) ? 0 : name.hashCode());
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (!(obj instanceof AbstractMemberHandle)) 
		return false;
	    AbstractMemberHandle other = (AbstractMemberHandle) obj;
	    if (className == null) {
		if (other.className != null)
		    return false;
	    } else if (!className.equals(other.className))
		return false;
	    if (desc == null) {
		if (other.desc != null)
		    return false;
	    } else if (!desc.equals(other.desc))
		return false;
	    if (name == null) {
		if (other.name != null)
		    return false;
	    } else if (!name.equals(other.name))
		return false;
	    return true;
	}
		
	public String getSignature() {
		return name+(desc != null ? desc.substring(0, desc.lastIndexOf(")")+1) : "");
	}
}
