package org.pure4j.test.checker.corner_cases.return_type;

import org.pure4j.Pure4J;
import org.pure4j.test.ShouldBePure;

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

	@ShouldBePure
	@Override
	public int hashCode() {
		return 6;
	}

	@ShouldBePure
	@Override
	public String toString() {
		return "blah";
	}
	
	
}
