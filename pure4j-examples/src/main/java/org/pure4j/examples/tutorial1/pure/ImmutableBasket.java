package org.pure4j.examples.tutorial1.pure;

import java.util.Map;

import org.pure4j.collections.IPersistentMap;
import org.pure4j.immutable.AbstractImmutableValue;

public class ImmutableBasket extends AbstractImmutableValue<ImmutableBasket>{
	
	private final IPersistentMap<Product, Integer> contents;
	
	public ImmutableBasket(IPersistentMap<Product, Integer> contents) {
		this.contents = contents;
	}
	
	public float priceBasket(IPersistentMap<Product, Float> priceList, float taxRate) {
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
	

	public ImmutableBasket updateProduct(Product p, int i) {
		if (i == 0) {
			return new ImmutableBasket(contents.without(p));
		} else if (i>0) {
			return new ImmutableBasket(contents.assoc(p, i));
		} else {
			throw new IllegalArgumentException("You can't have negative product in the basket! ");
		}
	}

	@Override
	protected void fields(Visitor v, ImmutableBasket other) {
		v.visit(contents, other.contents);
	}
}
