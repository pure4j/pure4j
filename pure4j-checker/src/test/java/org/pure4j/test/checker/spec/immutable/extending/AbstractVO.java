package org.pure4j.test.checker.spec.immutable.extending;

import org.pure4j.test.checker.support.ShouldBePure;

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
