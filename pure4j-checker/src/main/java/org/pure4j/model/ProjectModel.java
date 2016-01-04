package org.pure4j.model;

import java.util.List;
import java.util.Set;

import org.pure4j.model.impl.AnnotationHandle;
import org.pure4j.model.impl.ClassHandle;

/**
 * Returns information about the java project which can only be ascertained by bytecode scanning 
 * the project structure.
 * 
 * @author moffatr
 *
 */
public interface ProjectModel {

	public ClassHandle getClassHandle(String className);
	
    public List<CallHandle> getCalls(DeclarationHandle m);
    
    public Set<DeclarationHandle> getCalledBy(CallHandle m);
    
    public Set<String> getSubclasses(String className);
    
    public Set<String> getClassesWithAnnotation(String annotationName);
    
    public Set<AnnotatedElementHandle> getMembersWithAnnotation(String annotationName);
    
    public Set<String> getDependsOnClasses(String className);
    
    public Set<String> getDependedOnClasses(String className);

    public boolean withinModel(String className);
    
    public boolean packageWithinModel(String packageName);
    
    public Set<String> getClassesInPackage(String packageName);
    
    public Set<String> getAllClasses();
    
    public Set<DeclarationHandle> getAllDeclaredMethods();

    public Set<DeclarationHandle> getDeclaredMethods(String className);
    
    public Set<AnnotationHandle> getAnnotationReferences(String className);
    
    public CallInfo getOpcodes(DeclarationHandle ch);

	public ClassHandle getClassHandle(CallHandle handle);
    
}
