package org.pure4j;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.immutable.RuntimeImmutabilityChecker;


/**
 * 
 * @author robmoffat
 *
 */
public class Pure4J {

	public static final int SOME_PRIME = 31;

	@Pure
	public static int hashCode(Object a) {
		Pure4J.immutable(a);
		return a == null ? 0 : a.hashCode();
	}
	
	@Pure
	public static int hashCode(Object a, Object b) {
		return hashCode(a) * SOME_PRIME + hashCode(b);
	}
	
	@Pure
	public static int hashCode(Object a, Object b, Object c) {
		return ((hashCode(a) * SOME_PRIME) + hashCode(b) * SOME_PRIME) + hashCode(c);
	}
		
	@Pure
	public static int hashCode(Object a, Object b, Object c, Object d) {
		return (((hashCode(a) * SOME_PRIME) + hashCode(b) * SOME_PRIME) + hashCode(c)) * SOME_PRIME + hashCode(d);
	}
	
	@Pure
	public static int hashCode(Object... a) {
		int out = 1;
		for (int i = 0; i < a.length; i++) {
			out = out * (hashCode(a) * SOME_PRIME);
		}
		return out;
	}
	
	@Pure
	public static final void immutable(Object a) {
		immutableClass(a);
	}
	
	@Pure
	public static final Object returnImmutable(Object o) {
		immutableClass(o);
		return o;
	}
	
	private static void immutableClass(Object o) {
		if (o == null) {
			return;
		}
		
		RuntimeImmutabilityChecker.throwIfClassNotImmutable(o.getClass());
	}

	@Pure
	public static final void immutable(Object a, Object b) {
		immutableClass(a);
		immutableClass(b);
		
	}
	
	@Pure
	public static final void immutable(Object a, Object b, Object c) {
		immutableClass(a);
		immutableClass(b);
		immutableClass(c);
	}
	
	@Pure
	public static final void immutable(Object a, Object b, Object c, Object d) {
		immutableClass(a);
		immutableClass(b);
		immutableClass(c);
		immutableClass(d);
	}
	
	@Pure
	public static String toString(Object o, Object... fields) {
		StringBuilder sb = new StringBuilder();
		sb.append(o.getClass().getSimpleName());
		sb.append("[");
		for (int i = 0; i < fields.length; i++) {
			if (fields[i] != null) {
				sb.append(fields[i]);
				if (i < fields.length-1) {
					sb.append(",");
				}
			}
		}
		
		sb.append("]");
		return sb.toString();
	}
	
	@Pure
	public static final UnsupportedOperationException unsupported() {
		throw new UnsupportedOperationException();
	}
	
	@Pure
	public static boolean equals(Object a1, Object b1) {
		if (a1==b1) {
			return true;
		} else if (a1 == null) {
			return false;
		} else {
			return a1.equals(b1);
		}
	}
	
	@Pure
	public static boolean equals(Object a1, Object a2, Object b1, Object b2) {
		return equals(a1, b1) && equals(a2, b2);
	}
	
	@Pure
	public static boolean equals(Object a1, Object a2, Object a3, Object b1, Object b2, Object b3) {
		return equals(a1, b1) && equals(a2, b2) && equals(a3, b3);
	}
	
	/**
	 * Handy equals builder.  Feel free to use or not. Other equals-builders are available.
	 * @param parts An array with even number of elements, where the first n/2 elements are from the  first object to be compared, and the second n/2 are from the other object.
	 * @return true if all the part-pairs are equal, else false.
	 */
	@Pure
	public static boolean equals(Object... parts) {
		if (parts.length % 2 != 0) {
			throw new IllegalArgumentException("Should have even number of parameters");
		}
		int half = parts.length / 2;
		for (int i = 0; i < half; i++) {
			if (!equals(parts[i], parts[i+half])) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Checks that the elements in an array (if not the array itself) are all immutable.
	 * Works quickly O(1) if the base element type is itself immutable.  Otherwise checks
	 * each element in O(n) time.
	 * @param fields fields to be checked
	 */
	@Pure
	public static void immutableArray(Object[] fields) {
		Class<?> cl = fields.getClass();
		Class<?> component = cl.getComponentType();
		if (RuntimeImmutabilityChecker.isClassImmutable(component)) {
			return;
		}
		
		// ok, we have to check each element in turn
		for (int i = 0; i < fields.length; i++) {
			component = fields[i].getClass();
			RuntimeImmutabilityChecker.throwIfClassNotImmutable(component);
		}
	}
}
