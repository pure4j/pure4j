package org.pure4j.processor;

import java.lang.reflect.Type;

public interface ClassAnnotationCache {

	boolean classIsMarked(Class<?> in, Callback cb);

	boolean typeIsMarked(Type t, Callback cb);

	void doClassChecks(Class<?> immutableClass, Callback cb);

}