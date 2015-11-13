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

	private static final class HashCodeVisitor implements Visitor {
		int result = 0;
		
		@Override
		public void visit(Object o, Object o2) {
			result = (result * Pure4J.SOME_PRIME) + o.hashCode();
		}
	}
	
	private static class EqualsVisitor implements Visitor {
		boolean result = true;
		
		@Override
		public void visit(Object o, Object o2) {
			if (result != false) {
				result = Pure4J.equals(o, o2);
			}
		}
	}
	
	private static class ToStringVisitor implements Visitor {
		
		StringBuilder in;
		boolean addComma = false;
		
		private ToStringVisitor(StringBuilder in) {
			this.in = in;
		}
		
		public void visit(Object o, Object o2) {
			if (addComma) {
				in.append(",");
			}
			if (o != null) {
				in.append(o.toString());
			}
			addComma = true;
		}
	}
	
	private static class ComparisonVisitor implements Visitor {
		
		int result = 0;
		
		@SuppressWarnings("unchecked")
		public void visit(Object o, Object o2) {
			if (result == 0) {
				if (o instanceof Comparable) {
					if (o2 == null) {
						result = -1;
					} else {
						if (o2.getClass() == o.getClass()) {
							result = ((Comparable<Object>) o).compareTo(o2);
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int hashCode() {
		HashCodeVisitor v = new HashCodeVisitor();
		fields(v, (M) this);
		return v.result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj == this) {
			return true;
		} else if (obj.getClass() == this.getClass()) {
			final EqualsVisitor v = new EqualsVisitor();
			fields(v, (M) obj);
			return v.result;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder();
		out.append(this.getClass().getName()+"[");
		fields(new ToStringVisitor(out), (M) this);
		out.append("]");
		return out.toString();
	}

	/**
	 * Plug in all the fields of your object here, and then they will be included in
	 * equals, hashcode and toString.
	 * @param v pass your fields into this.  will not be null.
	 * @param other the object to compare with or, "this" in the case of hashCode, toString.
	 */
	protected abstract void fields(Visitor v, M other);

	@Override
	public int compareTo(M o) {
		ComparisonVisitor cv = new ComparisonVisitor();
		fields(cv, o);
		return cv.result;
	}
}
