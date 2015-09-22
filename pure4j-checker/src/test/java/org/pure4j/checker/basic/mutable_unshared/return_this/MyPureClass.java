package org.pure4j.checker.basic.mutable_unshared.return_this;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.PureMethodParameterNotImmutableException;

@MutableUnshared
public class MyPureClass {
	
	private int[] someState;

	@CausesError(PureMethodParameterNotImmutableException.class) // You can only pass immutables into a MutableShared
	public MyPureClass(int[] someState) {
		super();
		this.someState = someState;
	}

	@Override
	@ShouldBePure
	public int hashCode() {
		return 55;
	}

	@ShouldBePure
	public MyPureClass somePureMethod() {
		return this;
	}
	
	@ShouldBePure
	public MyPureClass someImpureMethod() {
		// even this is ok, because someState can't be passed in here - mutable state cannot be passed.
		return new MyPureClass(someState);
	}
	
}
