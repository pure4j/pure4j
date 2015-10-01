package org.pure4j.test.checker.spec.mutable_unshared.override;

import org.pure4j.annotations.mutable.MutableUnshared;

@MutableUnshared
public abstract class BadBase {

	public Object somethingNotPure() {
		return new Object().hashCode();
	}
}
