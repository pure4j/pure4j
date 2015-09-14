package org.pure4j.checker.basic.pure.classes;

import java.util.Random;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.PureMethodAccessesSharedFieldException;
import org.pure4j.exception.PureMethodCallsImpureException;

@Pure
public class SomePureClass {
	
	public int state_too_public = 0;
	private int state_private = 0;
	
	
	@CausesError(PureMethodAccessesSharedFieldException.class)
	public SomePureClass() {
		super();
	}

	@ShouldBePure
	public Object returningThisIsPure() {
		// questionable.  Since the object isn't a value object, "this" is potentially
		// different for each pure construction.   However, when we construct the pure 
		// object, "this" is what is returned.  So, you kind of have to allow it.  
		// I think "this" is probably ok, because there is no way (without calling hashCode/
		// equals/toString) to decide whether one "this" is different to another "this"...
		// But in all those cases, you will be checking the purity of those functions, anyway.
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

