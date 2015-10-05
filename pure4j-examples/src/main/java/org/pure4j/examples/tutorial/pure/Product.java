package org.pure4j.examples.tutorial.pure;


public class Product {

	long sku;

	String description;
	
	public Product(long sku, String description) {
		super();
		this.sku = sku;
		this.description = description;
	}

	public long getSku() {
		return sku;
	}

	public void setSku(long sku) {
		this.sku = sku;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
