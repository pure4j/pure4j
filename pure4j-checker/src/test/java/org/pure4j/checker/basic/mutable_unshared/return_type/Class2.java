package org.pure4j.checker.basic.mutable_unshared.return_type;

import org.pure4j.Pure4J;
import org.pure4j.checker.basic.support.ShouldBePure;

public class Class2 implements Class1 {

	@ShouldBePure
	public Class2 process(Object in) {
		Pure4J.immutable(in);
		return new Class2();
	}
	
	@ShouldBePure
	public String p2(Object in) {
		Pure4J.immutable(in);
		return "cow";
	}
}
