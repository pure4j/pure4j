package org.pure4j.model;

import org.pure4j.model.impl.AnnotationHandle;
import org.pure4j.model.impl.ClassHandle;



public interface AnnotatedElementHandle extends Handle {
	
	public AnnotationHandle getAnnotation(Class<?> forClass);
	
	public ClassHandle getDeclaringClass();
}
