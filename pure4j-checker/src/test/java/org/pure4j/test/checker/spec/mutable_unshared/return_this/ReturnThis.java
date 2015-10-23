package org.pure4j.test.checker.spec.mutable_unshared.return_this;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.exception.PureMethodParameterNotImmutableException;
import org.pure4j.test.CausesError;
import org.pure4j.test.ShouldBePure;

@MutableUnshared
public class ReturnThis {
	
	private int[] someState;

	@CausesError(PureMethodParameterNotImmutableException.class) // You can only pass immutables into a MutableShared
	public ReturnThis(int[] someState) {
		super();
		this.someState = someState;
	}

	@Override
	@ShouldBePure
	public int hashCode() {
		return 55;
	}

	@ShouldBePure
	public ReturnThis somePureMethod() {
		return this;
	}
	
	@ShouldBePure
	public ReturnThis someImpureMethod() {
		// even this is ok, because someState can't be passed in here - mutable state cannot be passed.
		return new ReturnThis(someState);
	}
	
}
