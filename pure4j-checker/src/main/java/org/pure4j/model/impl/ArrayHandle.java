package org.pure4j.model.impl;

import org.pure4j.model.GenericTypeHandle;

public class ArrayHandle implements GenericTypeHandle {

	private int dimensions;
	
	public ArrayHandle(int dimensions, GenericTypeHandle element) {
		super();
		this.dimensions = dimensions;
		this.element = element;
	}

	private GenericTypeHandle element;

	public int getDimensions() {
		return dimensions;
	}

	public GenericTypeHandle getElement() {
		return element;
	}

	@Override
	public boolean isAssignableFrom(GenericTypeHandle handle) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isArray() {
		return true;
	}

	@Override
	public boolean isEnum() {
		return false;
	}
	
}
