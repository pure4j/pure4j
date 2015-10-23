package org.pure4j.test;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

public class AbstractTest {

	@Pure(Enforcement.FORCE)
	public static void assertEquals(Object exp, Object act) {
		if ((exp == null) && (act == null)) {
			return;
		}
		if (!exp.equals(act)) {
			throw new RuntimeException("Was expecting equality: "+exp+" and "+act);
		}
	}

	@Pure(Enforcement.FORCE)
	public static void log(String s) {
		System.out.println(s);
	}

	public AbstractTest() {
		super();
	}

}