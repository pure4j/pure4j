package org.pure4j.maven;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.pure4j.exception.Pure4JException;
import org.pure4j.processor.Callback;

public class MojoCallback implements Callback {

	Log l;
	boolean hasErrors = false;
	
	public boolean hasErrors() {
		return hasErrors;
	}

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
		hasErrors = true;
	}

	@Override
	public void registerPure(String signature, Boolean intf, Boolean impl) {
		pures.add("Marked Pure: "+signature);
	}
	
	List<String> pures = new ArrayList<String>(300);

	public void listPures() {
		Collections.sort(pures);
		for (String string : pures) {
			l.info(string);
		}
	}
}
