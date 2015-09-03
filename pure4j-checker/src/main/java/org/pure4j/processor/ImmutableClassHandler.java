package org.pure4j.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
			cb.send("Checking Class For @ImmutableValue: " + in.getName());
			boolean immutable = false;
			ImmutableValue ann = in.getAnnotation(ImmutableValue.class);
			immutable = immutable || (ann != null);
			immutable = immutable || ((in.getSuperclass() != null) ? classIsMarkedImmutable(in.getSuperclass(), cb) : false);
			for (Class<?> interf : in.getInterfaces()) {
				immutable = immutable || classIsMarkedImmutable(interf, cb);
			}
			
			if (immutable) {
				cb.send("Found immutable class: "+in.getName());
			}
			immutableClasses.put(in.getName(), immutable);
			return immutable;
		} else {
			return immutableClasses.get(in.getName());
		}
	}

	public boolean typeIsMarkedImmutable(Type t, Callback cb) {
		// check if it's in the immutable list
		Class<?> c;
		if (t instanceof Class) {
			c = (Class<?>) t;
		} else if (t instanceof Enum<?>){
			c = ((Enum) t).getDeclaringClass();
		} else  {
			throw new RuntimeException("Need some generics support");
		}
		
		
		return classIsMarkedImmutable(c, cb);
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
		if (!Modifier.isFinal(immutableClass.getModifiers())) {
			cb.registerError("Concrete @ImmutableValue class should be final: "+immutableClass.getName(), null);
		}
		
		while (immutableClass != Object.class) {
			for (Field f : immutableClass.getDeclaredFields()) {
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
