package org.pure4j.processor;

import java.lang.reflect.Type;

import org.pure4j.model.ProjectModel;

public interface ClassAnnotationCache {

	boolean classIsMarked(Class<?> in, Callback cb);

	boolean typeIsMarked(Type t, Callback cb);

	void doClassChecks(Class<?> immutableClass, Callback cb, ProjectModel pm);

	void addClass(Class<?> pureClass);

}