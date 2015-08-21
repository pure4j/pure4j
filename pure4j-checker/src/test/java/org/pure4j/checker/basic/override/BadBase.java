package org.pure4j.checker.basic.override;

public class BadBase {

	public int somethingNotPure() {
		return new Object().hashCode();
	}
}
