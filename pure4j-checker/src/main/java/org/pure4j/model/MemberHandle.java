package org.pure4j.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;


/**
 * Handle to a class member (method or field).
 * Also contains static utility functions for converting from string name of class back into
 * Class object, and doing the same with methods and fields.
 * 
 * @author moffatr
 * 
 */
public abstract class MemberHandle extends AbstractHandle<AccessibleObject> implements AnnotatedElementHandle<AccessibleObject> {

	protected String className;
	protected String name;
	protected String desc;

	public String getClassName() {
		return className;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	@Override
	public String toString() {
		return className + "." + name + (desc == null ? "" : desc);
	}

	public int compareTo(AnnotatedElementHandle<?> oo) {
	    if (oo instanceof MemberHandle) {
		MemberHandle o = (MemberHandle) oo;
		int res = this.getName().compareTo(o.getName());
		if (res==0) {
			if (this.getDesc()!=null) {
				return (this.getDesc().compareTo(o.getDesc()));
			} else if (o.getDesc()==null) {
				return 0;
			} else {
				return 1;
			}
			
		}
		
		return res;
	    } else {
		return 1;
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
	    if (getClass() != obj.getClass())
		return false;
	    MemberHandle other = (MemberHandle) obj;
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
	
	public abstract AccessibleObject hydrate(ClassLoader cl);
	
	public abstract <T extends Annotation> T getAnnotation(ClassLoader cl, Class<T> c);
	
	public abstract java.lang.reflect.Type[] getGenericTypes(ClassLoader cl);
	
	public String getSignature() {
		return name+desc;
	}
}
