package org.pure4j.checker.basic.mutable_unshared.override;

import org.pure4j.annotations.mutable.MutableUnshared;

@MutableUnshared
public abstract class BadBase {

	public int somethingNotPure() {
		return new Object().hashCode();
	}
}
