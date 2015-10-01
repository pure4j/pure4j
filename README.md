[![Build Status](https://travis-ci.org/robmoffat/pure4j.svg?branch=master)](https://travis-ci.org/robmoffat/pure4j)

Pure4J
------

Compile-Time Purity and Immutability Semantics For The Java Language

Forces
------

* Code correctness is a primary concern (more so than productivity).
* You are working in Java Language environment.  
* Parts of the codebase should be isolated and treated in a “pure” way for testing.
* You want to ensure your code is testable.
* You want to be able to reuse your code in other contexts, and know that it is dependency-free.

What is Function Purity?
------------------------

* The function always evaluates the same result value given the same argument value(s). i.e. the results are deterministic.
* Evaluation of the result does not cause any semantically observable side effect or output. Such as output to I/O devices.

See [wikipedia](https://en.wikipedia.org/wiki/Pure_function) for further details.

Islands Of Purity
-----------------

One of the main reasons for using Java as a programming language is because of its library support for integration with other systems,
e.g. JDBC, XML, Web-Services, JMS etc.  Pure4J allows you to write "islands" of pure code within the context of a larger, side-effecting code-base.


Major Components Of Pure4J
--------------------------

### 1.  Annotations

You apply these annotations to your code to indicate the purity constraints.  

* `@Pure` is applied on a method level, and indicates that the method is deterministic and has no side effects.  
* `@ImmutableValue` is applied on a class-level.  It indicates that the class is immutable after construction.   Also it indicates that the instance methods on the class are @Pure.  
* `@MutableUnshared` is also applied at class-level.  It indicates that the class contains some mutable state, however, that mutable state is never leaves the class:  all method arguments are immutable and return types are immutable too.  

There are various other supporting annotations, but these are the main ones.  Specification for each is here.

### 2.  Maven Purity Checker

As part of the build pipeline, you add the purity checker.  If any of the purity semantics indicated by the annotations are broken, errors occur.  

### 3. Persistent Collections

Persistent versions of the Java Collections library are provided.  These fulfil the `@ImmutableValue` contract, and return a new collection whenever a mutating operation is applied.  This means that you can construct your @ImmutableValue object with a collection of Strings (say) and be sure that the collection is also immutable.  

NB.  These are versions of the Clojure Persistent Collections, developed by Rich Hickey, modified for use with generics and to have regular constructors, and 
some alterations to the inheritance hiearchy.  

Also provided are subclasses of existing Java Collections, having the `@MutableUnshared` contract.  

### 4.  Knowledge of Java Language Purity

There are many methods in the Java language which are already pure (no side effects, deterministic) and many classes which produce immutable objects (e.g. String, Integer, LocalDate).   

This library keeps track of these and allows your pure functions to use existing pure functions in Java.

Tutorial
--------

See the tutorial [here](tutorial.md). 

There is an example project in the `pure4j-examples` folder which builds has some example use cases, and builds them using the Maven 
Plugin.  If you are starting a project and want to use Pure4J, start by looking at this.

Status of this Project
----------------------

This is currently an idea under investigation.  It’s quite possible that the contracts and semantics provided by this project will change in the future as new use cases are discovered.   

### Known Remaining Issues

* Persistent Collection Construction:  Currently, lots of static methods to construct.  Need to provide constructors to make it
more like idiomatic Java.  -- need to write tests for each constructor, I think.
* Bintray
* Tutorial

* Error Messages: currently quite hard to understand. Should include a list of potential solutions for each.
* Cascading Implementation Impurity: One impure function should 'taint' those that use it.
* Thorough review of purity of Java language classes
* Service-classes:  need a way to specify their purity without calling them `@ImmutableValue`s  -- not sure about this


