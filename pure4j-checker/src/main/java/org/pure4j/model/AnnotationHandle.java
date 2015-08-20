package org.pure4j.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;


/**
 * An annotation handle is used to describe an occasion where an annotation on a class, method or
 * field refers to another class using a parameter.
 * 
 * @author moffatr
 *
 */
public class AnnotationHandle extends AbstractHandle<Annotation> implements Comparable<AnnotationHandle>{
    
    public AnnotationHandle(String annotationField, AnnotatedElementHandle<?> annotatedItem, String annotationClass) {
	super();
	this.annotationField = annotationField;
	this.annotatedItem = annotatedItem;
	this.annotationClass = annotationClass;
    }

    public AnnotationHandle(Annotation a, AnnotatedElementHandle<?> site, String field) {
	this.annotationField = field;
	this.annotatedItem = site;
	this.annotationClass = convertClassName(a.annotationType());
    }
    
    private String annotationField;
        
    private AnnotatedElementHandle<?> annotatedItem;

    private String annotationClass;

    @SuppressWarnings("unchecked")
    public Annotation hydrate(ClassLoader cl) {
	Class<Annotation> annCl = (Class<Annotation>) hydrateClass(annotationClass, cl);
	AnnotatedElement ae = annotatedItem.hydrate(cl);
	Annotation a = ae.getAnnotation(annCl);
	return a;
    }

    public String getAnnotationField() {
        return annotationField;
    }

    public AnnotatedElementHandle<?> getAnnotatedItem() {
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
	result = prime * result + ((annotationField == null) ? 0 : annotationField.hashCode());
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
	if (annotationField == null) {
	    if (other.annotationField != null)
		return false;
	} else if (!annotationField.equals(other.annotationField))
	    return false;
	return true;
    }

    public int compareTo(AnnotationHandle arg0) {
	int v = this.annotationField.compareTo(arg0.annotationField);
	if (v!=0)
	    return v;
	
	v = this.annotationClass.compareTo(arg0.annotationClass);
	if (v!=0)
	    return v;
	
	return this.annotatedItem.compareTo(arg0.annotatedItem);
    }
    
    
}
