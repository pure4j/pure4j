package org.pure4j.checker.basic.mutable_unshared.classes;

import java.util.Random;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.PureMethodAccessesSharedFieldException;
import org.pure4j.exception.PureMethodCallsImpureException;
import org.pure4j.exception.PureMethodReturnNotImmutableException;

@MutableUnshared
public class SomePureClass {
	
	public int state_too_public = 0;
	private int state_private = 0;
	
	
	@CausesError(PureMethodAccessesSharedFieldException.class)
	public SomePureClass() {
		super();
	}

	@CausesError(PureMethodReturnNotImmutableException.class)
	public Object returningThisIsPure() {
		// "this" is an ok thing to return.  But, the return type is going to be tricky.
		return this;   
	}

	@ShouldBePure
	public int someOperation(int in) {
		return otherOp(in) + otherOp(in) + state_private;
	}
	
	@CausesError(PureMethodAccessesSharedFieldException.class)
	public int notPureEnough() {
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

