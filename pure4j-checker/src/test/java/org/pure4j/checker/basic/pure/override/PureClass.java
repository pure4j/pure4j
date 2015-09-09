package org.pure4j.checker.basic.pure.override;

import org.pure4j.annotations.pure.Pure;

@Pure
public class PureClass extends BadBase {

	@Override
	public int somethingNotPure() {
		return 6;	// now is pure
	}


	
}
