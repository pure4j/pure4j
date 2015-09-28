package org.pure4j.checker.spec.mutable_unshared.override;

import org.pure4j.checker.support.ShouldBePure;

public class PureClass extends BadBase {

	@Override
	@ShouldBePure
	public Integer somethingNotPure() {
		return 6;	// now is pure
	}


	
}
