package org.pure4j.checker.basic.immutable.extending;

public abstract class AbstractVO {

	protected final int bob;

	public int getBob() {
		return bob;
	}

	public AbstractVO(int bob) {
		super();
		this.bob = bob;
	}
	
}
