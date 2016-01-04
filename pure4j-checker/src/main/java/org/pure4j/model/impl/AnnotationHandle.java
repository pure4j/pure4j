package org.pure4j.model.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.pure4j.model.AnnotatedElementHandle;
import org.pure4j.model.Handle;

/**
 * An annotation handle is used to describe an occasion where an annotation on a
 * class, method or field refers to another class using a parameter.
 * 
 * @author moffatr
 *
 */
public class AnnotationHandle extends AbstractHandle {

	public AnnotationHandle(AnnotatedElementHandle annotatedItem, String annotationClass) {
		super();
		this.annotatedItem = annotatedItem;
		this.annotationClass = annotationClass;
	}

	private AnnotatedElementHandle annotatedItem;

	private String annotationClass;
	private Map<String, List<Object>> fields = new HashMap<String, List<Object>>();

	public AnnotatedElementHandle getAnnotatedItem() {
		return annotatedItem;
	}

	public String getAnnotationClass() {
		return annotationClass;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotatedItem == null) ? 0 : annotatedItem.hashCode());
		result = prime * result + ((annotationClass == null) ? 0 : annotationClass.hashCode());
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
		AnnotationHandle other = (AnnotationHandle) obj;
		if (annotatedItem == null) {
			if (other.annotatedItem != null)
				return false;
		} else if (!annotatedItem.equals(other.annotatedItem))
			return false;
		if (annotationClass == null) {
			if (other.annotationClass != null)
				return false;
		} else if (!annotationClass.equals(other.annotationClass))
			return false;
		return true;
	}

	public int compareTo(Handle arg0) {
		if (arg0 instanceof AnnotationHandle) {
			int v = this.annotationClass.compareTo(((AnnotationHandle)arg0).annotationClass);
			if (v != 0)
				return v;

			return this.annotatedItem.compareTo(((AnnotationHandle)arg0).annotatedItem);
		} else {
			return this.getClass().getName().compareTo(arg0.getClass().getName());
		}
	}

	public List<Object> getField(String string) {
		return fields.get(string);
	}
	
	public void addField(String name, Object o) {
		List<Object> toAddTo = fields.get(name);
		if (toAddTo == null) {
			toAddTo = new LinkedList<Object>();
			fields.put(name, toAddTo);
		}
		toAddTo.add(o);
	}

}
