package org.pure4j.checker.pure_class;

import java.util.Random;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

@Pure
public class SomePureClass {
	
	int state = 0;

	public int someOperation(int in) {
		return otherOp(in) + otherOp(in) + state;
	}

	private int otherOp(int in) {
		return in*3;
	}
	
	public Object returningThisIsNotPure() {
		return this;
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

