package org.pure4j.immutable;

import org.pure4j.annotations.pure.Pure;

public class EqualsHelp {

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
