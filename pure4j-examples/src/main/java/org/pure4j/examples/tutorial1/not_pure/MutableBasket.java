package org.pure4j.examples.tutorial1.not_pure;

import java.util.Map;

import org.pure4j.Pure4J;
import org.pure4j.examples.tutorial1.pure.Product;

public class MutableBasket {
	
	private final Map<Product, Integer> contents;
	
	public MutableBasket(Map<Product, Integer> initialContents) {
		this.contents = initialContents;
	}
	
	public MutableBasket() {
		contents = null;
	}
	
	public void updateProduct(Product p, int i) {
		if (i == 0) {
			contents.remove(p);
		} else if (i>0) {
			contents.put(p, i);
		} else {
			throw new IllegalArgumentException("You can't have negative product in the basket! ");
		}
	}

	// @Pure -- can't be pure, because of the non-persistent map, and accessing "contents"
	public float priceBasket(Map<Product, Float> priceList, float taxRate) {
		Pure4J.immutable(priceList);
		float runningTotal = 0f;
		for (Map.Entry<Product, Integer> item : contents.entrySet()) {
			Float price = priceList.get(item.getKey());
			
			if (price == null) {
				throw new RuntimeException("No price for "+item.getKey());
			}
			
			runningTotal += price * item.getValue(); // quantity
		}
		
		return runningTotal + (runningTotal * taxRate);
	}
//	
//	
//	@Test 
//	public void checkThisPackage() throws IOException {
//		checkThisPackage(this.getClass(), 1);
//	}
//	

}
