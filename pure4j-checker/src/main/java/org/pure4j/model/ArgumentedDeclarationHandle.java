package org.pure4j.model;

/**
 * Details of the arguments of the declaration
 * 
 * @author robmoffat
 *
 */
public interface ArgumentedDeclarationHandle extends DeclarationHandle {

	public GenericTypeHandle[] getGenericTypes();
	
}
