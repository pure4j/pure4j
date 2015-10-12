package org.pure4j.immutable;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.chrono.IsoChronology;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.pure.Pure;


public class RuntimeImmutabilityChecker {

	/**
	 * Set this system property to <code>false</code> to disable immutability checking at runtime.
	 */
	public static final String PURE4J_IMMUTABILITY_CHECKING = "pure4j.immutability_checking";
	
	public static final Set<String> INBUILT_IMMUTABLE_CLASSES = createInbuiltImmutableSet();

	public static Set<String> createInbuiltImmutableSet() {
		HashSet<String> out = new HashSet<String>();
		List<Class<?>> l = Arrays.asList((Class<?>) Byte.class, Float.class, Double.class, Integer.class, String.class, Character.class, 
			Long.class, Boolean.class, Short.class, BigDecimal.class, BigInteger.class, Currency.class, Void.class);
		
		for (Class<?> c : l) {
			out.add(c.getName());
		}
		
		// these are added this way in case we are running in a JVM without them (<1.7)
		out.add("java.time.LocalDate");
		out.add("java.time.LocalDateTime");
		out.add("java.time.LocalTime");
		out.add("java.time.MonthDay");
		out.add("java.time.IsoChronology");
	
		return Collections.unmodifiableSet(out);
	}
	
	private static Map<Class<?>, Boolean> immutableClassCache = new HashMap<Class<?>, Boolean>(100);
	
	@Pure
	public static void throwIfClassNotImmutable(Class<?> immutableClass) {
		if (INBUILT_IMMUTABLE_CLASSES.contains(immutableClass.getName())) {
			return;
		}

		Boolean imm = immutableClassCache.get(immutableClass);
		if(Boolean.TRUE.equals(imm)) {
			return ;
		}
	
		String prop = System.getProperty(PURE4J_IMMUTABILITY_CHECKING);
		if ((prop != null) && ("false".equals(prop.toLowerCase()))) {
			return;
		}
		
		if (immutableClass.isArray()) {
			immutableClassCache.put(immutableClass, false);
			throw new ClassNotImmutableException("Array type passed:  not immutable. "+immutableClass);
		}
		
		if (classImmutableValueAnnotation(immutableClass) == null) {
			immutableClassCache.put(immutableClass, false);
			throw new ClassNotImmutableException("Class is missing @ImmutableValue annotation "+immutableClass);
		}
		
		immutableClassCache.put(immutableClass, true);
	}
	
	@Pure
	public static boolean isClassImmutable(Class<?> immutableClass) {
		if (INBUILT_IMMUTABLE_CLASSES.contains(immutableClass.getName())) {
			return true; 
		}

		Boolean imm = immutableClassCache.get(immutableClass);
		if(imm != null) {
			return imm;
		}
	
		if (immutableClass.isArray()) {
			return false;
		}
		
		if (classImmutableValueAnnotation(immutableClass) != null) {
			immutableClassCache.put(immutableClass, true);
			return true;
		}
		
		immutableClassCache.put(immutableClass, false);
		return false;
	}
	
	public static ImmutableValue classImmutableValueAnnotation(Class<?> immutableClass) {
		if ((immutableClass == Object.class) || (immutableClass == null)) {
			return null;
		}
		
		ImmutableValue ann = immutableClass.getAnnotation(ImmutableValue.class);
		if (ann != null) {
			return ann;
		} else {
			for (Class<?> interf : immutableClass.getInterfaces()) {
				ann = classImmutableValueAnnotation(interf);
				if (ann != null) {
					return ann;
				}
			}
			return classImmutableValueAnnotation(immutableClass.getSuperclass());
		}
	}
	
	public static <X> X classHierarchyAnnotation(Class<?> immutableClass, Class<X> ac) {
		if ((immutableClass == Object.class) || (immutableClass == null)) {
			return null;
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		X ann = (X) immutableClass.getAnnotation((Class) ac);
		if (ann != null) {
			return ann;
		} else {
			for (Class<?> interf : immutableClass.getInterfaces()) {
				ann = classHierarchyAnnotation(interf, ac);
				if (ann != null) {
					return ann;
				}
			}
			return classHierarchyAnnotation(immutableClass.getSuperclass(), ac);
		}
	}
	
	
	
	
	public static void throwIfTypeNotImmutable(Type t) {
		if (t instanceof Class) {
			throwIfClassNotImmutable((Class<?>) t);
		} else if (t instanceof ParameterizedType) {
			throw new ClassNotImmutableException("Type not supported: " + t);
		} else if (t instanceof TypeVariable) {
			throw new ClassNotImmutableException("Type not supported: " + t);
		} else if (t instanceof GenericArrayType) {
			throw new ClassNotImmutableException("Type not supported: " + t);
		} else if (t instanceof WildcardType) {
			throw new ClassNotImmutableException("Type not supported: " + t);
		} else {
			throw new ClassNotImmutableException("Type not supported: " + t);
		}
	}
}
