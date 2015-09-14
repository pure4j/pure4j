package org.pure4j.checker.basic.pure.methods;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.immutable.good.SomeValueObject;
import org.pure4j.checker.basic.support.ShouldBePure;

public class SomePureStuff {

	@Pure
	@ShouldBePure
	public static int getLetterCount(String in) {
		return in.length();
	}
	
	@Pure
	@ShouldBePure
	public static int doStuffWithValueObject(SomeValueObject svo) {
		return svo.getNameLength()+svo.getAge()+getLetterCount("blah");
	}
}
