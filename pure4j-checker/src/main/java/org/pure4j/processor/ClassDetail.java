package org.pure4j.processor;

import java.util.HashSet;
import java.util.Set;

public class ClassDetail {

	public String superClass;
	
	public Set<String> interfaces = new HashSet<String>();

	public Set<String> annotations = new HashSet<String>();
}
