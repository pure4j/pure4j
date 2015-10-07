package org.pure4j.test.checker.corner_cases.non_immutable_class;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.exception.PureMethodAccessesNonImmutableFieldException;
import org.pure4j.exception.PureMethodOnNonImmutableClassException;
import org.pure4j.test.checker.support.AbstractChecker;
import org.pure4j.test.checker.support.CausesError;

public class SomeNonImmutable extends AbstractChecker {

	public String someState;
	
	@CausesError(PureMethodOnNonImmutableClassException.class)
	@Pure
	public String someChangingState(String in) {
		return in + someState;
	}
	
	@CausesError(PureMethodOnNonImmutableClassException.class)
	@Pure
	public String mapIn(Map<String, String> in, float f) {
		Pure4J.immutable(in);
		return in.toString()+someState+f;
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}
