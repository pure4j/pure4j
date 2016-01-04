package org.pure4j.processor;

import org.pure4j.model.GenericTypeHandle;
import org.pure4j.model.ProjectModel;
import org.pure4j.model.impl.ClassHandle;

public interface ClassAnnotationCache {
	
	boolean classIsMarked(ClassHandle in, Callback cb, ProjectModel pm);

	boolean typeIsMarked(GenericTypeHandle t, Callback cb, ProjectModel pm);

	void doClassChecks(ClassHandle immutableClass, Callback cb, ProjectModel pm);

	void addClass(ClassHandle pureClass);

}