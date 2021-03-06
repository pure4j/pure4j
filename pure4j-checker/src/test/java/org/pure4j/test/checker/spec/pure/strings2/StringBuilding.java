package org.pure4j.test.checker.spec.pure.strings2;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.test.ShouldBePure;
import org.pure4j.test.checker.spec.immutable.good.SomeGoodValueObject;

public class StringBuilding {

	@Pure
	@ShouldBePure
	public static String soSomeMorePureStuff(SomeGoodValueObject a, String b) {
		return a + b;		// this should be fine, as both are immutable
	}
	
}
