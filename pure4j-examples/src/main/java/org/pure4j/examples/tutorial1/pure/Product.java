package org.pure4j.examples.tutorial1.pure;

import org.pure4j.annotations.immutable.ImmutableValue;

@ImmutableValue
public class Product {

	private final long sku;

	private final String description;
	
	public Product(long sku, String description) {
		super();
		this.sku = sku;
		this.description = description;
	}

	public long getSku() {
		return sku;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + (int) (sku ^ (sku >>> 32));
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
		Product other = (Product) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (sku != other.sku)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Product [sku=" + sku + ", description=" + description + "]";
	}

	
}
