package org.pure4j.processor;

import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.exception.ClassNotFinalException;
import org.pure4j.exception.FieldNotFinalException;
import org.pure4j.exception.FieldTypeNotImmutableException;
import org.pure4j.immutable.RuntimeImmutabilityChecker;
import org.pure4j.model.ProjectModel;
import org.pure4j.model.impl.AnnotationHandle;
import org.pure4j.model.impl.ClassHandle;
import org.pure4j.model.impl.FieldDeclarationHandle;

/**
 * Keeps track of the classes that have been registered as immutable
 * @author robmoffat
 *
 */
public class ImmutableValueClassHandler extends AbstractClassAnnotationCache implements ClassAnnotationCache {

	public static final boolean CHECK_FOR_FINAL_CLASSES = false;
	
	
	@Override
	public void doClassChecks(ClassHandle immutableClass, Callback cb, ProjectModel pm) {
		if (CHECK_FOR_FINAL_CLASSES) {
			if (!immutableClass.isFinal()) {
				cb.registerError(new ClassNotFinalException(immutableClass));
			}
		}
		
		
		while ((immutableClass != null) && (!immutableClass.isObject())) {
			for (FieldDeclarationHandle f : immutableClass.getDeclaredFields()) {
				AnnotationHandle fieldIV = f.getAnnotation(IgnoreImmutableTypeCheck.class);
				boolean skip = false;
				skip |= (fieldIV != null);
				skip |= (immutableClass.isEnum() && f.getName().equals("ENUM$VALUES"));
				
				if (!skip) {
					if (!f.isStatic()) {
						if (!f.isFinal()) {
							cb.registerError(new FieldNotFinalException(f));
						}
					
						if (!typeIsMarked(f.getGenericType(), cb, pm)) {
							cb.registerError(new FieldTypeNotImmutableException(f, immutableClass));
						}
					}
				}
					
			}
			
			immutableClass = pm.getClassHandle(immutableClass.getSuperclass());
		}
	}
	
	@Override
	public boolean classIsMarked(ClassHandle in, Callback cb, ProjectModel pm) {
		if ((in == null) || in.isObject()) {
			return false;
		}
	
		if (in.isPrimitive()) {
			return true;
		}
		
		if (RuntimeImmutabilityChecker.INBUILT_IMMUTABLE_CLASSES.contains(in.getClassName())) {
			return true;
		}
		
		if (!classMap.containsKey(in.getClassName())) {
			boolean immutable = false;
			AnnotationHandle ann = AbstractClassAnnotationCache.classHierarchyAnnotation(in, ImmutableValue.class, pm);
			immutable = (ann != null);
	
			if (immutable) {
				cb.send("@ImmutableValue: "+in.getClassName());
			} 
			classMap.put(in.getClassName(), immutable);
			return immutable;
		} else {
			return classMap.get(in.getClassName());
		}
	}

}
