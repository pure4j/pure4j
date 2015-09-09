package org.pure4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.Util;
import org.pure4j.immutable.RuntimeImmutabilityChecker;


/**
 * 
 * @author robmoffat
 *
 */
@Pure
public class Pure4J {

	private static final int SOME_PRIME = 31;

	@Pure
	public static int hashCode(Object a) {
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
	
	
	public static final void immutable(Object a) {
		RuntimeImmutabilityChecker.throwIfClassNotImmutable(a.getClass());
	}
	
	public static final void immutable(Object a, Object b) {
		RuntimeImmutabilityChecker.throwIfClassNotImmutable(a.getClass());
		RuntimeImmutabilityChecker.throwIfClassNotImmutable(b.getClass());
		
	}
	
	public static final void immutable(Object a, Object b, Object c) {
		RuntimeImmutabilityChecker.throwIfClassNotImmutable(a.getClass());
		RuntimeImmutabilityChecker.throwIfClassNotImmutable(b.getClass());
		RuntimeImmutabilityChecker.throwIfClassNotImmutable(c.getClass());
	}
	
	public static final void immutable(Object a, Object b, Object c, Object d) {
		RuntimeImmutabilityChecker.throwIfClassNotImmutable(a.getClass());
		RuntimeImmutabilityChecker.throwIfClassNotImmutable(b.getClass());
		RuntimeImmutabilityChecker.throwIfClassNotImmutable(c.getClass());
		RuntimeImmutabilityChecker.throwIfClassNotImmutable(d.getClass());
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
	
	/**
	 * TODO:  make this run really really fast so that no-one needs to
	 * implement a specific equals method for @ImmutableValue classes ever again.
	 * We can surely reach the performance of specific code with some bytecode malarkey.
	 */
	@Pure
	public static boolean equals(Object a, Object b) {
		if (a == b)
			return true;
		if (b == null)
			return false;
		if (a.getClass() != b.getClass())
			return false;

		Class<?> cl = a.getClass();
		try {
			if (cl != Object.class) {
				for (Field f : cl.getDeclaredFields()) {
					if ((!Modifier.isStatic(f.getModifiers())) && (!Modifier.isTransient(f.getModifiers()))) {
						f.setAccessible(true);
						Object aa = f.get(a);
						Object bb = f.get(b);
						if (!equals(aa, bb)) {
							return false;
						}
						
					}
				}
				cl = cl.getSuperclass();
			}
		return true;
		} catch (Exception e) {
			throw new RuntimeException("Couldn't reflectively determine fields: ", e);
		}
	}
}
