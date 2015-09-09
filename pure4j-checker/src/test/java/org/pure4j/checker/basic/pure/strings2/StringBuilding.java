package org.pure4j.checker.basic.pure.strings2;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.immutable.good.SomeValueObject;

public class StringBuilding {

	@Pure
	public static String soSomeMorePureStuff(SomeValueObject a, String b) {
		return a + b;
	}
	
}
