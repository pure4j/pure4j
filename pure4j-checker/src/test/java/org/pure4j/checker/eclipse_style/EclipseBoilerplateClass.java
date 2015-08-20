package org.pure4j.checker.eclipse_style;

import org.pure4j.annotations.immutable.ImmutableValue;

@ImmutableValue
public final class EclipseBoilerplateClass {

	final String a;
	final int b;

	public EclipseBoilerplateClass(String a, int b) {
		super();
		this.a = a;
		this.b = b;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + b;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EclipseBoilerplateClass other = (EclipseBoilerplateClass) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (b != other.b)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SomeImmutableValue [a=" + a + ", b=" + b + "]";
	}

	public String getA() {
		return a;
	}
	public int getB() {
		return b;
	}

}
