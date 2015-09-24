package org.pure4j.examples.var_model.pure;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.collections.PersistentList;

@ImmutableValue
public class SomeImmutableThing {

	public final String fieldA;
	public String getFieldA() {
		return fieldA;
	}

	public int getB() {
		return b;
	}

	public PersistentList<String> getSomeList() {
		return someList;
	}

	private final int b;
	private final PersistentList<String> someList;
	
	public SomeImmutableThing(String fieldA, int b, PersistentList<String> someList) {
		super();
		this.fieldA = fieldA;
		this.b = b;
		this.someList = someList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + b;
		result = prime * result + ((fieldA == null) ? 0 : fieldA.hashCode());
		result = prime * result + ((someList == null) ? 0 : someList.hashCode());
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
		SomeImmutableThing other = (SomeImmutableThing) obj;
		if (b != other.b)
			return false;
		if (fieldA == null) {
			if (other.fieldA != null)
				return false;
		} else if (!fieldA.equals(other.fieldA))
			return false;
		if (someList == null) {
			if (other.someList != null)
				return false;
		} else if (!someList.equals(other.someList))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SomeImmutableThing [fieldA=" + fieldA + ", b=" + b + ", someList=" + someList + "]";
	}

}
