package org.pure4j.test.checker.spec.mutable_unshared.inherit;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.test.ShouldBePure;

@MutableUnshared
public class BasePure {

	@ShouldBePure
	public int onBasePure() {
		return 4;
	}
}
