package org.pure4j.processor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
import org.pure4j.annotations.pure.Pure;
import org.pure4j.model.ConstructorHandle;
import org.pure4j.model.MethodHandle;

public class ClassLibrary {

	public static final Set<?> INBUILT_IMMUTABLE_CLASSES = new HashSet<Object>();
	
	private Set<String> pureList = PureList.loadPureLists();
	
	private ClassLoader cl;

	public ClassLibrary(ClassLoader cl) {
		super();
		this.cl = cl;
	}

	static {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		List l = Arrays.asList(Byte.class, Float.class, Double.class, Integer.class, String.class, Character.class, 
			Long.class, Boolean.class, Short.class, BigDecimal.class, BigInteger.class);
		INBUILT_IMMUTABLE_CLASSES.addAll(l);
	}
	
	private Map<Class<?>, Boolean> immutableClasses = new HashMap<Class<?>, Boolean>();
	private Map<Class<?>, Boolean> pureClasses = new HashMap<Class<?>, Boolean>();

	public boolean classIsPure(Class<?> in, Callback cb) {
		if ((in == null) || (in == Object.class)) {
			return false;
		}
		
		if (pureClasses.containsKey(in)) {
			return pureClasses.get(in);
		}
		
		cb.send("Checking for @Pure: "+in.getCanonicalName());
		
		Pure p = in.getAnnotation(Pure.class);
		if ((p != null) && (p.value() != Enforcement.NOT_PURE)) {
			pureClasses.put(in, true);
			return true;
		}
		
		for (Class<?> intf : in.getInterfaces()) {
			if (classIsPure(intf, cb)) {
				pureClasses.put(in, true);
				return true;
			}
		}
		
		if (classIsPure(in.getSuperclass(), cb)) {
			pureClasses.put(in, true);
			return true;
		}
		
		pureClasses.put(in, false);
		return false;
	}
	
	public boolean classIsImmutable(Class<?> in, Callback cb) {
		if ((in == null) || (in == Object.class)) {
			return false;
		}

		if (in.isPrimitive()) {
			return true;
		}
		
		if (INBUILT_IMMUTABLE_CLASSES.contains(in)) {
			return true;
		}
		
		
		if (!immutableClasses.containsKey(in.getCanonicalName())) {
			cb.send("Checking Class For @ImmutableValue: " + in.getCanonicalName());
			boolean immutable = false;
			ImmutableValue ann = in.getAnnotation(ImmutableValue.class);
			immutable = immutable || (ann != null);
			immutable = immutable || ((in.getSuperclass() != null) ? classIsImmutable(in.getSuperclass(), cb) : false);
			for (Class<?> interf : in.getInterfaces()) {
				immutable = immutable || classIsImmutable(interf, cb);
			}
			
			if (immutable) {
				cb.send("Found immutable class: "+in.getCanonicalName());
				immutableClasses.put(in, immutable);
			}
			return immutable;
		} else {
			return immutableClasses.get(in.getCanonicalName());
		}
	}
	
	public boolean typeIsImmutable(Type t, Callback cb) {
		// check if it's in the immutable list
		Class<?> c;
		if (t instanceof Class) {
			c = (Class<?>) t;
		} else {
			// todo
			c = null;
		}
		
		
		return classIsImmutable(c, cb);
	}

	public boolean isPureCallOutsideProjectScope(String string) {
		return pureList.contains(string);
	}
	
	public boolean isPureCall(ConstructorHandle mh, Callback cb) {
		if (isPureCallOutsideProjectScope(mh.toString())) {
			return true;
		}
		
		Constructor<?> m = mh.hydrate(cl);
		Pure p = m.getAnnotation(Pure.class);
		if (p != null) {
			return p.value() != Enforcement.NOT_PURE;
		}
		
		p = m.getDeclaringClass().getAnnotation(Pure.class);
		if (p != null) {
			return p.value() != Enforcement.NOT_PURE;
		}
		
		if (classIsImmutable(m.getDeclaringClass(), cb)) {
			return true;
		}
		
		if (PurityChecker.IGNORE_EXCEPTION_CONSTRUCTION) {
			if (Throwable.class.isAssignableFrom(m.getDeclaringClass())) {
				return true;
			}
		}
		
		return false;
	}

	public boolean isPureCall(MethodHandle mh, Callback cb) {
		if (pureList.contains(mh.toString())) {
			return true;
		}
		
		if (PurityChecker.IGNORE_EQUALS_PARAMETER_PURITY) {
			if (("equals".equals(mh.getName())) && ("(Ljava/lang/Object;)Z".equals(mh.getDesc()))) {
				return true;
			}
		}
		
		Method m = mh.hydrate(cl);
		Pure p = m.getAnnotation(Pure.class);
		if (p != null) {
			return p.value() != Enforcement.NOT_PURE;
		}
		
		p = m.getDeclaringClass().getAnnotation(Pure.class);
		if (p != null) {
			return p.value() != Enforcement.NOT_PURE;
		}
		
		if (classIsImmutable(m.getDeclaringClass(), cb)) {
			return true;
		}
		
		return false;
	}

}
