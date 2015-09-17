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

    /**
     * Returns a list of all the methods & fields that a given method calls, within the model scope
     */
    public List<MemberHandle> getCalls(MemberHandle m);
    
    /** 
     * Returns a set of methods a given method or field is called by, within the project scope
     */
    public Set<MemberHandle> getCalledBy(MemberHandle m);
    
    /**
     * Returns the subclasses defined within the project scope, of a given class.
     */
    public Set<String> getSubclasses(String className);
    
    /**
     * Returns all the classes within the project having a given annotation
     */
    public Set<String> getClassesWithAnnotation(String annotationName);
    
    /**
     * Returns fields and methods defined within the project with a given annotation
     */
    public Set<MemberHandle> getMembersWithAnnotation(String annotationName);
    
    /**
     * Gets the immediate (i.e. non-transitive) dependencies of a given class, defined within
     * the project scope.
     */
    public Set<String> getDependsOnClasses(String className);
    
    /**
     * Gets the classes in the model that depend on this class
     */
    public Set<String> getDependedOnClasses(String className);

    /**
     * Gets the immediate (i.e. non-transitive) dependencies of a given package, defined within
     * the project scope.
     */
    public Set<PackageHandle> getDependsOnPackages(PackageHandle packageName);
    
    /**
     * Gets the classes in the model that depend on this class
     */
    public Set<PackageHandle> getDependedOnPackages(PackageHandle packageName);    
    
    /**
     * Returns true if the class is within the scanned part of the project
     */
    public boolean withinModel(String className);
    
    /**
     * Returns true if the package is within the scanned part of the project
     */
    public boolean packageWithinModel(String packageName);
    
    /**
     * Returns the classNames within the package.
     */
    public Set<String> getClassesInPackage(String packageName);
    
    /**
     * Returns the classes in the model, in the order they were added
     */
    public Set<String> getAllClasses();
    
    /**
     * Returns the annotations that refer to this class
     */
    public Set<AnnotationHandle> getAnnotationReferences(String className);
    
    /**
     * Return the opcodes for this definition
     */
    public int getOpcodes(CallHandle ch);
    
}
