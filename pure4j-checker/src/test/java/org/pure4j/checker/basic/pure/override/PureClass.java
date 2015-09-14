package org.pure4j.checker.basic.pure.override;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.ShouldBePure;

@Pure
public class PureClass extends BadBase {

	@Override
	@ShouldBePure
	public int somethingNotPure() {
		return 6;	// now is pure
	}


	
}
