package org.pure4j.immutable;

import org.pure4j.annotations.pure.Pure;

public class HashHelp {

	private static final int SOME_PRIME = 31;

	@Pure
	public static int hashCode(Object a, Object b) {
		return hashCode(a) * SOME_PRIME + hashCode(b);
	}
	
	@Pure
	public static int hashCode(Object a, Object b, Object c) {
		return ((hashCode(a) * SOME_PRIME) + hashCode(b) * 31) + hashCode(c);
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
	public static int hashCode(Object a) {
		if (a == null) {
			return 0;
		} else {
			return 1;
		}
	}
}
