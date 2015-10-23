package org.pure4j.test.checker.spec.mutable_unshared.override;

import org.pure4j.test.ShouldBePure;

public class PureClass extends BadBase {

	@Override
	@ShouldBePure
	public Integer somethingNotPure() {
		return 6;	// now is pure
	}


	
}
