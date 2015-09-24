package org.pure4j.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.exception.ClassNotFinalException;
import org.pure4j.exception.FieldTypeNotImmutableException;
import org.pure4j.immutable.RuntimeImmutabilityChecker;

/**
 * Keeps track of the classes that have been registered as immutable
 * @author robmoffat
 *
 */
public class MutableUnsharedClassHandler extends AbstractClassAnnoatationCache implements ClassAnnotationCache {

	public static final boolean CHECK_FOR_FINAL_CLASSES = false;
	
	private ClassAnnotationCache immutableHandler;
	
	public MutableUnsharedClassHandler(ClassAnnotationCache ivch) {
		this.immutableHandler = ivch;
	}
	
	@Override
	public void doClassChecks(Class<?> mutableSharedClass, Callback cb) {
		
		if (CHECK_FOR_FINAL_CLASSES) {
			if (!Modifier.isFinal(mutableSharedClass.getModifiers())) {
				cb.registerError(new ClassNotFinalException(mutableSharedClass));
			}
		}
		
		while (mutableSharedClass != Object.class) {
			for (Field f : mutableSharedClass.getDeclaredFields()) {
				if (!Modifier.isStatic(f.getModifiers())) {
					boolean pub = Modifier.isPublic(f.getModifiers());
					boolean priv = Modifier.isPrivate(f.getModifiers());
					boolean prot = Modifier.isProtected(f.getModifiers());
					boolean accessible = pub || ((!priv) && (!prot));
					if (accessible) {
						if (!immutableHandler.typeIsMarked(f.getGenericType(), cb)) {
							cb.registerError(new FieldTypeNotImmutableException(f, mutableSharedClass));
						}
					}
				}
			}
			
			mutableSharedClass = mutableSharedClass.getSuperclass();
		}
		
	}
	
	@Override
	public boolean classIsMarked(Class<?> in, Callback cb) {
		if (!classMap.containsKey(in.getName())) {
			MutableUnshared ann = RuntimeImmutabilityChecker.classHierarchyAnnotation(in, MutableUnshared.class);
			boolean mu = (ann != null);
	
			if (mu) {
				cb.send("@MutableUnshared "+in.getName());
			} 
			classMap.put(in.getName(), mu);
			return mu;
		} else {
			return classMap.get(in.getName());
		}
	}
}
