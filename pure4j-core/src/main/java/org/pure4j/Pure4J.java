package org.pure4j;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.Util;
import org.pure4j.immutable.RuntimeImmutabilityChecker;


/**
 * 
 * @author robmoffat
 *
 */
public class Pure4J {

	private static final int SOME_PRIME = 31;

	@Pure
	public static int hashCode(Object a) {
		Pure4J.immutable(a);
		return Util.hash(a);
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
	
	/**
	 * TODO:  add non-varargs version of this.
	 */
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
		return new UnsupportedOperationException();
	}
}
