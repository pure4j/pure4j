package org.pure4j.checker.spec.mutable_unshared.visitor;

import org.pure4j.Pure4J;
import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.support.ShouldBePure;

public class VisitorExample {
	
	@MutableUnshared
	public interface Visitor {
		
		
		public void visit(Object o);
		
	}

	public static class Impl1 implements Visitor {
		
		@SuppressWarnings("unused")
		private int someState;

		@ShouldBePure
		@Override
		public void visit(Object o) {
			Pure4J.immutable(o);
			someState ++;
		}
		
	}
	
	@ShouldBePure
	@Pure
	public void doSomething() {
		Visitor v = new Impl1();
		String on = new String();
		v.visit(on);
	}

}
