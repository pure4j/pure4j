package org.pure4j.immutable;

import org.pure4j.annotations.pure.Pure;

/**
 * Pure version of {@link StringBuilder}.  Probably temporary.
 * 
 * @author robmoffat
 */
public class StringHelp {

	@Pure
	public static String conc(String... args) {
		StringBuilder sb = new StringBuilder();
		for (String string : args) {
			sb.append(string);
		}
		
		return sb.toString();
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
}
