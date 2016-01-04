package org.pure4j.exception;

import org.pure4j.model.impl.ClassHandle;
import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class ClassHierarchyUsesBothTypesOfPurity extends Pure4JException {

	public ClassHierarchyUsesBothTypesOfPurity(PureMethod pm, ClassHandle usedIn) {
		super("Both @Immutable and @MutableUnshared types of purity for classes: \n"+pm.getUsedIn()+" \n and: \n"+usedIn);
	}

}
