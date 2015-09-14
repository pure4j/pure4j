package org.pure4j.processor;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.exception.ClassNotFinalException;
import org.pure4j.exception.FieldNotFinalException;
import org.pure4j.exception.FieldTypeNotImmutableException;
import org.pure4j.immutable.RuntimeImmutabilityChecker;

/**
 * Keeps track of the classes that have been registered as immutable
 * @author robmoffat
 *
 */
public class ImmutableClassHandler {

	public static final boolean CHECK_FOR_FINAL_CLASSES = false;
	
	private Map<String, Boolean> immutableClasses = new HashMap<String, Boolean>();
	
	public boolean classIsMarkedImmutable(Class<?> in, Callback cb) {
		if ((in == null) || (in == Object.class)) {
			return false;
		}

		if (in.isPrimitive()) {
			return true;
		}
		
		if (RuntimeImmutabilityChecker.INBUILT_IMMUTABLE_CLASSES.contains(in)) {
			return true;
		}
		
		if (!immutableClasses.containsKey(in.getName())) {
			boolean immutable = false;
			ImmutableValue ann = RuntimeImmutabilityChecker.classImmutableValueAnnotation(in);
			immutable = (ann != null);

			if (immutable) {
				cb.send("immutable:           "+in.getName());
			} else {
				cb.send("not immutable:       "+in.getName());
			}
			immutableClasses.put(in.getName(), immutable);
			return immutable;
		} else {
			return immutableClasses.get(in.getName());
		}
	}

	public boolean typeIsMarkedImmutable(Type t, Callback cb) {
		if (t instanceof Class) {
			if (((Class<?>) t).isArray()) {
				return false;
			} else {
				return classIsMarkedImmutable((Class<?>) t, cb);
			}
		} else if (t instanceof Enum<?>){
			return true;
		} else if (t instanceof ParameterizedType) {
			// we need to make sure the raw class is immutable
			Type raw = ((ParameterizedType) t).getRawType();
			if (!typeIsMarkedImmutable(raw, cb)) {
				return false;
			}
			
//			for (Type t2 : ((ParameterizedType) t).getActualTypeArguments()) {
//				if (!typeIsMarkedImmutable(t2, cb)) {
//					return false;
//				}
//			}
			
			return true;
		} else if (t instanceof TypeVariable) {
			TypeVariable<?> tv = (TypeVariable<?>) t;
			for (Type b : tv.getBounds()) {
				if (!typeIsMarkedImmutable(b, cb)) {
					return false;
				}
			}
			return true;
		} else if (t instanceof GenericArrayType) {
			return false;
		} else {
			throw new RuntimeException("Type not supported: "+t);
		}
	}
	
	/**
	 * Adds the class to the list of immutable classes.  Without force, will check that the class 
	 * is immutable or not first.
	 */
	public void checkAddImmutableClass(Class<?> c, Callback cb, boolean force) {
		if (force || classIsMarkedImmutable(c, cb)) {
			immutableClasses.put(c.getName(), true);
		}
	}
		
	public void doClassImmutabilityChecks(Class<?> immutableClass, Callback cb) {
		ImmutableValue iv = RuntimeImmutabilityChecker.classImmutableValueAnnotation(immutableClass);
		if ((iv != null) && (iv.value() == Enforcement.FORCE)) {
			// accept that it's immutable, even when it's not
			return;
		}
		
		if (CHECK_FOR_FINAL_CLASSES) {
			if (!Modifier.isFinal(immutableClass.getModifiers())) {
				cb.registerError(new ClassNotFinalException(immutableClass));
			}
		}
		
		while (immutableClass != Object.class) {
			for (Field f : immutableClass.getDeclaredFields()) {
				ImmutableValue fieldIV = f.getAnnotation(ImmutableValue.class);
				boolean skip = false;
				skip |= ((fieldIV != null) && (fieldIV.value() == Enforcement.FORCE));
				skip |= (immutableClass.isEnum() && f.getName().equals("ENUM$VALUES"));
				
				if (!skip) {
					if (!Modifier.isStatic(f.getModifiers())) {
						if (!Modifier.isFinal(f.getModifiers())) {
							cb.registerError(new FieldNotFinalException(f));
						}
					}
					
					if (!typeIsMarkedImmutable(f.getGenericType(), cb)) {
						cb.registerError(new FieldTypeNotImmutableException(f));
					}
				}
					
			}
			
			immutableClass = immutableClass.getSuperclass();
		}
	}
	
	
	public void checkAddImmutableClasses(Set<Class<?>> modelClasses, Callback cb) {
		for (Class<?> class1 : modelClasses) {
			if (classIsMarkedImmutable(class1, cb)) {
				doClassImmutabilityChecks(class1, cb);
			}
		}
	}
}
