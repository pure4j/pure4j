package org.pure4j.checker.spec.pure.bad_references.client;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.spec.pure.bad_references.supplier.SomePure;
import org.pure4j.checker.support.CausesError;
import org.pure4j.exception.PureMethodCallsImpureException;

public class TestBadLibraryReference {

	@CausesError(PureMethodCallsImpureException.class)
	@Pure
	public void doLibraryCall() {
		SomePure somePure = new SomePure();
		somePure.someOkCall();
		somePure.toString();
		somePure.someNotPureCall();
	}
}
