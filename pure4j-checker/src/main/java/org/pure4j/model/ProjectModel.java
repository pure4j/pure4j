package org.pure4j.model;

import java.util.List;
import java.util.Set;

/**
 * Returns information about the java project which can only be ascertained by bytecode scanning 
 * the project structure.
 * 
 * @author moffatr
 *
 */
public interface ProjectModel {

    public List<MemberHandle> getCalls(MemberHandle m);
    
    public Set<MemberHandle> getCalledBy(MemberHandle m);
    
    public Set<String> getSubclasses(String className);
    
    public Set<String> getClassesWithAnnotation(String annotationName);
    
    public Set<MemberHandle> getMembersWithAnnotation(String annotationName);
    
    public Set<String> getDependsOnClasses(String className);
    
    public Set<String> getDependedOnClasses(String className);

    public Set<PackageHandle> getDependsOnPackages(PackageHandle packageName);
    
    public Set<PackageHandle> getDependedOnPackages(PackageHandle packageName);    

    public boolean withinModel(String className);
    
    public boolean packageWithinModel(String packageName);
    
    public Set<String> getClassesInPackage(String packageName);
    
    public Set<String> getAllClasses();
    
    public Set<MemberHandle> getAllDeclaredMethods();

    public Set<MemberHandle> getDeclaredMethods(String className);
    
    public Set<AnnotationHandle> getAnnotationReferences(String className);
    
    public CallInfo getOpcodes(MemberHandle ch);
    
}
