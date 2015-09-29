package org.pure4j.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.exception.ClassNotFinalException;
import org.pure4j.exception.FieldNotFinalException;
import org.pure4j.exception.FieldTypeNotImmutableException;
import org.pure4j.immutable.RuntimeImmutabilityChecker;
import org.pure4j.model.ProjectModel;
import org.springframework.asm.Type;

/**
 * Keeps track of the classes that have been registered as immutable
 * @author robmoffat
 *
 */
public class ImmutableValueClassHandler extends AbstractClassAnnoatationCache implements ClassAnnotationCache {

	public static final boolean CHECK_FOR_FINAL_CLASSES = false;
	
	
	@Override
	public void doClassChecks(Class<?> immutableClass, Callback cb, ProjectModel pm) {
		if (CHECK_FOR_FINAL_CLASSES) {
			if (!Modifier.isFinal(immutableClass.getModifiers())) {
				cb.registerError(new ClassNotFinalException(immutableClass));
			}
		}
		
		
		while (immutableClass != Object.class) {
			for (Field f : immutableClass.getDeclaredFields()) {
				IgnoreImmutableTypeCheck fieldIV = f.getAnnotation(IgnoreImmutableTypeCheck.class);
				boolean skip = false;
				skip |= (fieldIV != null);
				skip |= (immutableClass.isEnum() && f.getName().equals("ENUM$VALUES"));
				
				if (!skip) {
					if (!Modifier.isStatic(f.getModifiers())) {
						if (!Modifier.isFinal(f.getModifiers())) {
							cb.registerError(new FieldNotFinalException(f));
						}
					
					
						if (!typeIsMarked(f.getGenericType(), cb)) {
							cb.registerError(new FieldTypeNotImmutableException(f, immutableClass));
						}
					}
				}
					
			}
			
			immutableClass = immutableClass.getSuperclass();
		}
	}
	
	@Override
	public boolean classIsMarked(Class<?> in, Callback cb) {
		if ((in == null) || (in == Object.class)) {
			return false;
		}
	
		if (in.isPrimitive()) {
			return true;
		}
		
		if (RuntimeImmutabilityChecker.INBUILT_IMMUTABLE_CLASSES.contains(in)) {
			return true;
		}
		
		if (!classMap.containsKey(in.getName())) {
			boolean immutable = false;
			ImmutableValue ann = RuntimeImmutabilityChecker.classImmutableValueAnnotation(in);
			immutable = (ann != null);
	
			if (immutable) {
				cb.send("@ImmutableValue: "+in.getName());
			} 
			classMap.put(in.getName(), immutable);
			return immutable;
		} else {
			return classMap.get(in.getName());
		}
	}
}
