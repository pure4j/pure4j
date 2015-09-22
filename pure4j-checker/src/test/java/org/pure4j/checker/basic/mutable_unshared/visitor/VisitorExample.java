package org.pure4j.checker.basic.mutable_unshared.visitor;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.Pure4J;
import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;

public class VisitorExample extends AbstractChecker {
	
	@MutableUnshared
	public interface Visitor {
		
		public void visit(Object o);
		
	}

	public static class Impl1 implements Visitor {
		
		private int someState;

		@Override
		public void visit(Object o) {
			Pure4J.immutable(o);
			someState ++;
		}
		
	}
	
	
	@Pure
	public void doSomething() {
		Visitor v = new Impl1();
		String on = new String();
		v.visit(on);
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 1, 2);
	}
}
