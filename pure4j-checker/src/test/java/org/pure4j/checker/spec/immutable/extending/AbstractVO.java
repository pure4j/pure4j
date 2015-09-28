package org.pure4j.checker.spec.immutable.extending;

import org.pure4j.checker.support.ShouldBePure;

public abstract class AbstractVO {

	protected final int bob;

	@ShouldBePure
	public int getBob() {
		return bob;
	}

	@ShouldBePure
	public AbstractVO(int bob) {
		super();
		this.bob = bob;
	}
	
}
