package org.pure4j.test.checker.spec.mutable_unshared.classes;

import java.util.Random;

import org.pure4j.Pure4J;
import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.exception.FieldTypeNotImmutableException;
import org.pure4j.exception.PureMethodCallsImpureException;
import org.pure4j.exception.PureMethodReturnNotImmutableException;
import org.pure4j.test.checker.support.CausesError;
import org.pure4j.test.checker.support.ShouldBePure;

@MutableUnshared
public class SomeMutableUnsharedClass {
	
	public int state_too_public = 0;
	private int state_private = 0;
	
	@CausesError(FieldTypeNotImmutableException.class)
	public Object some_non_immutable;

	@ShouldBePure
	public Object guardedReturnIsPure() {
		new Object();
		return Pure4J.returnImmutable(5);   
	}
	
	
	@CausesError(PureMethodReturnNotImmutableException.class)
	public Object returningThisIsCurrentlyNotPure() {
		return this;   
	}
	
	
	
	@CausesError(PureMethodReturnNotImmutableException.class)
	public Object returningObjectIsNotPure() {
		return new Object();
	}

	@ShouldBePure
	public int someOperation(int in) {
		return otherOp(in) + otherOp(in) + state_private;
	}
	
	@ShouldBePure
	public int pureAsItReturnsSomethingImmutable() {
		return state_too_public;
	}

	@ShouldBePure
	private int otherOp(int in) {
		return in*3;
	}
	
	
	
	@ShouldBePure
	public int somePureOperation(int x) {
		return nonPureForced() * x;
	}

	@Pure(Enforcement.FORCE)
	@ShouldBePure
	private int nonPureForced() {
		return new Random().nextInt(5);
	}
		
	@CausesError(PureMethodCallsImpureException.class)
	public int shouldntBePure() {
		return nonPureAndKnowsIt();
	}

	@Pure(Enforcement.NOT_PURE) 
	private int nonPureAndKnowsIt() {
		return new Random().nextInt(5);
	}
	
}

