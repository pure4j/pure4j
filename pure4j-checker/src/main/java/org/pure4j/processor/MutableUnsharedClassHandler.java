package org.pure4j.processor;

import java.lang.reflect.Modifier;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.exception.ClassNotFinalException;
import org.pure4j.immutable.RuntimeImmutabilityChecker;

/**
 * Keeps track of the classes that have been registered as immutable
 * @author robmoffat
 *
 */
public class MutableUnsharedClassHandler extends AbstractClassAnnoatationCache implements ClassAnnotationCache {

	public static final boolean CHECK_FOR_FINAL_CLASSES = false;
	
	@Override
	public void doClassChecks(Class<?> immutableClass, Callback cb) {
		
		if (CHECK_FOR_FINAL_CLASSES) {
			if (!Modifier.isFinal(immutableClass.getModifiers())) {
				cb.registerError(new ClassNotFinalException(immutableClass));
			}
		}
		
	}
	
	@Override
	public boolean classIsMarked(Class<?> in, Callback cb) {
		if (!classMap.containsKey(in.getName())) {
			MutableUnshared ann = RuntimeImmutabilityChecker.classHierarchyAnnotation(in, MutableUnshared.class);
			boolean mu = (ann != null);
	
			if (mu) {
				cb.send("mutable unshared:    "+in.getName());
			} else {
				cb.send("not mutable unshared:"+in.getName());
			}
			classMap.put(in.getName(), mu);
			return mu;
		} else {
			return classMap.get(in.getName());
		}
	}
}
