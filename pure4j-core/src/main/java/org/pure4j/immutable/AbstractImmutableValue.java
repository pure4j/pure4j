package org.pure4j.immutable;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.mutable.MutableUnshared;

/**
 * Save yourself the bother of writing all the boilerplate for your immutable
 * value class by extending this one.
 * 
 * @author robmoffat
 *
 */
@ImmutableValue
public abstract class AbstractImmutableValue<M> implements Comparable<M> {
	
	@MutableUnshared
	protected interface Visitor {
		
		public void visit(Object o, Object o2);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int hashCode() {
		final int[] hc = new int[1];
		fields(new Visitor() {

			@Override
			public void visit(Object o, Object o2) {
				hc[0] = (hc[0] * Pure4J.SOME_PRIME) + o.hashCode();
			}
			
		}, (M) this);
		
		return hc[0];
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj == this) {
			return true;
		} else if (obj.getClass() == this.getClass()) {
			final boolean[] result = { true };
			
			fields(new Visitor() {
				
				@Override
				public void visit(Object o, Object o2) {
					if (result[0] != false) {
						result[0] = o.equals(o2);
					}
				}
			}, (M) obj);
			
			return result[0];
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder();
		out.append(this.getClass().getName()+"[");
		fields(new Visitor() {

			@Override
			public void visit(Object o, Object o2) {
				out.append(o.toString());
			}
			
		}, (M) this);
		
		out.append("]");
		return out.toString();
	}

	/**
	 * Plug in all the fields of your object here, and then they will be included in
	 * equals, hashcode and toString.
	 */
	public abstract void fields(Visitor v, M other);

	@Override
	public int compareTo(M o) {
		int[] result = { 0 };
		
		fields(new Visitor() {

			@SuppressWarnings("unchecked")
			@Override
			public void visit(Object o, Object o2) {
				if (result[0] == 0) {
					if (o instanceof Comparable) {
						if (o2.getClass() == o.getClass()) {
							result[0] = ((Comparable<Object>) o).compareTo(o2);
						}
					}
				}
			}
			
		}, o);
		
		return result[0];
	}
}
