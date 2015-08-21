package org.pure4j.checker.pure_class;

import java.util.Random;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

@Pure
public class SomePureClass {
	
	int state_too_public = 0;
	private int state_private = 0;
	
	public Object returningThisIsPure() {
		// questionable.  Since the object isn't a value object, "this" is potentially
		// different for each pure construction.   However, when we construct the pure 
		// object, "this" is what is returned.  So, you kind of have to allow it.  
		// I think "this" is probably ok, because there is no way (without calling hashCode/
		// equals/toString) to decide whether one "this" is different to another "this"...
		// But in all those cases, you will be checking the purity of those functions, anyway.
		return this;   
	}

	public int someOperation(int in) {
		return otherOp(in) + otherOp(in) + state_private;
	}
	
	public int notPureEnough() {
		return state_too_public;
	}

	private int otherOp(int in) {
		return in*3;
	}
	
	
	
	public int somePureOperation(int x) {
		return nonPureForced() * x;
	}

	@Pure(Enforcement.FORCE)
	private int nonPureForced() {
		return new Random().nextInt(5);
	}
		
	public int shouldntBePure() {
		return nonPureAndKnowsIt();
	}

	@Pure(Enforcement.NOT_PURE) 
	private int nonPureAndKnowsIt() {
		return new Random().nextInt(5);
	}
	
}

