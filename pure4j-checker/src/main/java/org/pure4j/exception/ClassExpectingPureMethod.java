package org.pure4j.exception;

import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class ClassExpectingPureMethod extends Pure4JException {

	public ClassExpectingPureMethod(Class<?> c, PureMethod pm) {
		super("Class "+c.getName()+" was expecting a pure implementation, but uses inherited impure implementation\n"+pm);
	}

}
