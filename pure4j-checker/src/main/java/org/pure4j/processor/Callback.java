package org.pure4j.processor;

public interface Callback {
	
	public void send(String s);
	
	public void registerError(String s, Throwable optional);
	
}