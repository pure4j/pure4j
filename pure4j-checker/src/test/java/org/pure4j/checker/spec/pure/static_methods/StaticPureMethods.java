package org.pure4j.checker.spec.pure.static_methods;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.spec.immutable.good.SomeGoodValueObject;
import org.pure4j.checker.support.ShouldBePure;

public class StaticPureMethods {

	@Pure
	@ShouldBePure
	public static int getLetterCount(String in) {
		return in.length();
	}
	
	@Pure
	@ShouldBePure
	public static int doStuffWithValueObject(SomeGoodValueObject svo) {
		return svo.getNameLength()+svo.getAge()+getLetterCount("blah");
	}
}
