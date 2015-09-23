package org.pure4j.maven;

import org.apache.maven.plugin.logging.Log;
import org.pure4j.exception.Pure4JException;
import org.pure4j.processor.Callback;

public class MojoCallback implements Callback {

	Log l;
	
	public MojoCallback(Log log) {
		this.l = log;
	}

	@Override
	public void send(String s) {
		l.debug(s);
	}

	@Override
	public void registerError(Pure4JException optional) {
		l.error(optional.getMessage());
	}

	@Override
	public void registerPure(String signature) {
		l.info("Marked Pure: "+signature);
	}

}
