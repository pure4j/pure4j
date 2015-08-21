package org.pure4j.checker.override;

public class BadBase {

	public int somethingNotPure() {
		return new Object().hashCode();
	}
}
