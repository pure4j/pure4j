package org.pure4j;

import org.pure4j.annotations.pure.Pure;

@Pure
public class Pure4J {

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
	
	public static final void immutable(Object a) {
		// do nothing for now
	}
	
	public static final void immutable(Object a, Object b) {
		// do nothing for now
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
	public static boolean equals(Object a, Object b, Object... fields) {
		if (a == b)
			return true;
		if (b == null)
			return false;
		if (a.getClass() != b.getClass())
			return false;

		return true;		
	}
}
