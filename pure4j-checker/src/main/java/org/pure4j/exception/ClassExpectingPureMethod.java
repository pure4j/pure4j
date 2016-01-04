package org.pure4j.exception;

import org.pure4j.model.impl.ClassHandle;
import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class ClassExpectingPureMethod extends Pure4JException {

	public ClassExpectingPureMethod(ClassHandle c, PureMethod pm) {
		super("Class "+c.getClassName()+" was expecting a pure implementation, but uses inherited impure implementation\n"+pm);
	}

}
