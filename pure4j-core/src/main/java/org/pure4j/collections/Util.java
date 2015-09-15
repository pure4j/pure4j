/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Apr 19, 2008 */

package org.pure4j.collections;

import java.math.BigInteger;

import org.pure4j.annotations.pure.Pure;

public class Util {

	static public boolean identical(Object k1, Object k2) {
		return k1 == k2;
	}

	static public Class<?> classOf(Object x) {
		if (x != null)
			return x.getClass();
		return null;
	}

	@SuppressWarnings("unchecked")
	static public int compare(Object k1, Object k2) {
		if (k1 == k2)
			return 0;
		if (k1 != null) {
			if (k2 == null)
				return 1;
			return ((Comparable<Object>) k1).compareTo(k2);
		}
		return -1;
	}

	static public int hash(Object o) {
		if (o == null)
			return 0;
		return o.hashCode();
	}

	static public int hashCombine(int seed, int hash) {
		// a la boost
		seed ^= hash + 0x9e3779b9 + (seed << 6) + (seed >> 2);
		return seed;
	}

	static public boolean isPrimitive(Class<?> c) {
		return c != null && c.isPrimitive() && !(c == Void.TYPE);
	}

	static public boolean isInteger(Object x) {
		return x instanceof Integer || x instanceof Long 
				|| x instanceof BigInteger;
	}

	static public Object ret1(Object ret, Object nil) {
		return ret;
	}

	static public <K> ISeq<K> ret1(ISeq<K> ret, Object nil) {
		return ret;
	}

	@Pure
	static public RuntimeException runtimeException(String s) {
		return new RuntimeException(s);
	}

	static public RuntimeException runtimeException(String s, Throwable e) {
		return new RuntimeException(s, e);
	}

	/**
	 * Throw even checked exceptions without being required to declare them or
	 * catch them. Suggested idiom:
	 * <p>
	 * <code>throw sneakyThrow( some exception );</code>
	 */
	static public RuntimeException sneakyThrow(Throwable t) {
		// http://www.mail-archive.com/javaposse@googlegroups.com/msg05984.html
		if (t == null)
			throw new NullPointerException();
		Util.<RuntimeException> sneakyThrow0(t);
		return null;
	}

	@SuppressWarnings("unchecked")
	static private <T extends Throwable> void sneakyThrow0(Throwable t)
			throws T {
		throw (T) t;
	}

	@Pure
	static public boolean equals(Object k1, Object k2){
		if(k1 == k2)
			return true;
		return k1 != null && k1.equals(k2);
	}

}
