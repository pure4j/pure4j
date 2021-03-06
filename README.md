[![Build Status](https://travis-ci.org/robmoffat/pure4j.svg?branch=master)](https://travis-ci.org/robmoffat/pure4j)
[![Download](https://api.bintray.com/packages/robmoffat/maven/pure4j/images/download.svg) ](https://bintray.com/robmoffat/maven/pure4j/_latestVersion)
[![Coverage Status](https://coveralls.io/repos/robmoffat/pure4j/badge.svg?branch=master&service=github)](https://coveralls.io/github/robmoffat/pure4j?branch=master)

Pure4J
------

Compile-Time Purity and Immutability Semantics For The Java Language

**Please Note:** This project was an experiment:  While it achieves the goals of providing immutability and purity constraints for Java, coding using Pure4J is _hard work_.  If you're interested in playing with these concepts in Java then you've come to the right place, but having tried to build projects using this, I don't recommend using it in-the-large.  Feel free to experiment though.

Forces
------

* Code correctness is a primary concern (more so than productivity).
* You are working in Java Language environment.  
* Parts of the codebase should be isolated and treated in a “pure” way for testing.
* You want to ensure your code is testable.
* You want to be able to reuse your code in other contexts, and know that it is dependency-free.

Supporting Argument:
* [More here on why this *might* be a good idea.](docs/forces.md)
* [Some details on the background of this project.](docs/impetus.md)
* [Top 9 Reasons For Writing Pure, Functional Code In Java](docs/topx.md)

Quick Start
-----------

1.  Add the [maven plugin](#2--maven-purity-checker) to your java project.
2.  Add the [library](#3-persistent-collections) to your java project.
3.  Annotate some classes with [`@ImmutableValue`](#1--annotations).
4.  Run a `maven install` to see if your classes are pure.


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
* `@MutableUnshared` is also applied at class-level.  It indicates that the class contains some mutable state, however, that mutable state never leaves the class:  all method arguments are immutable and return types are immutable too.  

There are various other supporting annotations, but these are the main ones.  Specification for each is here.

### 2.  Maven Purity Checker

As part of the build pipeline, you add the purity checker.  If any of the purity semantics indicated by the annotations are broken, errors occur.  To add the Pure4J checker
to your build pipeline, add this to your Maven `pom.xml` file:

```xml
...
<build>
	<plugins>
		...
		<plugin>
			<groupId>org.pure4j</groupId>
			<artifactId>pure4j-maven-plugin</artifactId>
			<version>*latest version - see top of this file*</version>
			<executions>
	         	<execution>
                    <goals>
                        <goal>pure4j</goal>
                    </goals>   
		         </execution>
		     </executions>
		</plugin>
```

### 3. Persistent Collections

Persistent versions of the Java Collections library are provided.  These fulfil the `@ImmutableValue` contract, and return a new collection whenever a mutating operation is applied.  This means that you can construct your @ImmutableValue object with a collection of Strings (say) and be sure that the collection is also immutable.  

NB.  These are versions of the Clojure Persistent Collections, developed by Rich Hickey, modified for use with generics and to have regular constructors, and 
some alterations to the inheritance hiearchy.  

Import the annotations and the persistent collections into your project with the following dependency:

```xml
		<dependency>
			<groupId>org.pure4j</groupId>
			<artifactId>pure4j-core</artifactId>
	        <version>*see version info at top of this page*</version>
		</dependency>
```

Also provided are subclasses of existing Java Collections, having the `@MutableUnshared` contract.  

### 4.  Knowledge of Java Language Purity

There are many methods in the Java language which are already pure (no side effects, deterministic) and many classes which produce immutable objects (e.g. String, Integer, LocalDate).   

This library keeps track of these and allows your pure functions to use existing pure functions in Java.  

Tutorials
---------

* [See the first tutorial here](docs/tutorial1.md). 
* [Tutorial On Persistent Collections](docs/tutorial_collections.md).
* [About `@MutableUnshared`](docs/tutorial_mutable_unshared.md).
* [Financial Calculations In Pure4J, Tested Using Concordion](docs/tutorial2.md).

There is an example project in the `pure4j-examples` folder which builds has some example use cases, and builds them using the Maven 
Plugin.  If you are starting a project and want to use Pure4J, start by looking at this.


FAQ
---

Please see the FAQ [here](docs/FAQ.md).

Specifications
--------------

Concordion specifications detailing the interface of the project are available online [here](http://pure4j.github.io/pure4j/concordion/org/pure4j/test/checker/spec/Index.html).

Effectively this forms the Javadoc for the project, though the assertions made in the documentation are tested.

Status of this Project
----------------------

This is currently an idea under investigation.  It’s quite possible that the contracts and semantics provided by this project will change in the future as new use cases are discovered.  

Group
-----

[Visit the Google Group to join in the discussion](https://groups.google.com/forum/#!forum/pure4j)
 

Known Remaining Issues
----------------------

See [Github Issues Page](https://github.com/robmoffat/pure4j/issues)




