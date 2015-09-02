package org.pure4j.checker.basic.override;

import org.pure4j.annotations.pure.Pure;

@Pure
public class LeakyOverride extends BadBase {

	@Override
	public int somethingNotPure() {
		return super.somethingNotPure();	
	}

	public int somethingElse() {
		return super.somethingNotPure();	
	}
	
}
