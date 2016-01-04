package org.pure4j.model.impl;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractAnnotatedHandle extends AbstractHandle {

	List<AnnotationHandle> handles = new LinkedList<AnnotationHandle>();

	public AnnotationHandle getAnnotation(Class<?> forClass) {
		for (AnnotationHandle annotationHandle : handles) {
			if (annotationHandle.getAnnotationClass().equals(forClass.getName())) {
				return annotationHandle;
			}
		}
		
		return null;
	}
	
	public void addHandle(AnnotationHandle ah) {
		handles.add(ah);
	}
	
}
