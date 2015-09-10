package org.pure4j.processor;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.immutable.RuntimeImmutabilityChecker;

/**
 * Keeps track of the classes that have been registered as immutable
 * @author robmoffat
 *
 */
@SuppressWarnings("unchecked")
public class ImmutableClassHandler {

	public static final Set<?> INBUILT_IMMUTABLE_CLASSES = new HashSet<Object>();

	static {
		@SuppressWarnings({ "rawtypes" })
		List l = Arrays.asList(Byte.class, Float.class, Double.class, Integer.class, String.class, Character.class, 
			Long.class, Boolean.class, Short.class, BigDecimal.class, BigInteger.class, Math.class, StrictMath.class);
		INBUILT_IMMUTABLE_CLASSES.addAll(l);
	}
	
	private Map<String, Boolean> immutableClasses = new HashMap<String, Boolean>();
	
	public boolean classIsMarkedImmutable(Class<?> in, Callback cb) {
		if ((in == null) || (in == Object.class)) {
			return false;
		}

		if (in.isPrimitive()) {
			return true;
		}
		
		if (INBUILT_IMMUTABLE_CLASSES.contains(in)) {
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
		
		if (!Modifier.isFinal(immutableClass.getModifiers())) {
			cb.registerError("Concrete @ImmutableValue class should be final: "+immutableClass.getName(), null);
		}
		
		while (immutableClass != Object.class) {
			for (Field f : immutableClass.getDeclaredFields()) {
				ImmutableValue fieldIV = f.getAnnotation(ImmutableValue.class);
				
				if ((fieldIV == null) || (fieldIV.value() != Enforcement.FORCE)){
					if (!Modifier.isStatic(f.getModifiers())) {
						if (!Modifier.isFinal(f.getModifiers())) {
							cb.registerError("Field "+f.getName()+" should be final on @ImmutableValue class "+immutableClass.getName(), null);
						}
					}
					
					if (!typeIsMarkedImmutable(f.getGenericType(), cb)) {
						cb.registerError("Field "+f.getName()+" should have an immutable type on class "+
								immutableClass+".  Consider adding @ImmutableValue to "+f.getType(), null);
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
