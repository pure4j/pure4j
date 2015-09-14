package org.pure4j.processor;

import org.pure4j.exception.Pure4JException;

public interface Callback {
	
	public void send(String s);
	
	public void registerError(Pure4JException optional);
	
	public void registerPure(String signature);
	
}