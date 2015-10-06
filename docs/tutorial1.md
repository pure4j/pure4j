Pure4J First Tutorial
=====================

In this tutorial we are going to have a short introduction to building pure functions in Java, and checking that they're
pure with Pure4J.  For a description of why this might be a good idea, please review the [Why Pure4J](impetus.md) page, which 
discusses some of the problems this is trying to solve.

Scenario
--------

The use case we are going to consider here is for a calculator of a simple shopping basket, as follows:

1. The basket can contain various products.  It has a certain quantity for each product.  Each product has a name and an SKU (Stock-Keeping Unit) ID.
2. There is a price-list, which tells you how much each product costs (other price-lists may be used).
3. There is a fixed tax rate of 17.5%, which is calculated and added to the total of the basket.
4. We need to be able to add and remove things from the basket.

I.e. this is an extremely simple calculation indeed!  So, understanding this calculation is not going to be the hard part.

In fact, is it worth jumping through the hoops of making this pure?  (see the [FAQ](FAQ.md) for my answer to this)

Product First
-------------

Let's start by modelling the product.  We're going to need something like this:

```java
public class Product {

	String sku;
	String name;

}
```

There are 3 things wrong with this.   First, we haven't implemented `hashCode()` and `equals()`.  
But most IDE's can generate these for you, so it's not a problem.

Second, we haven't got getters / setters or a constructor.  Again, this is easily fixed -  IDEs now generate these.
I have prepared [this file](tutorial1_product_long.md) which shows you what this could look like.

Thirdly, and most importantly for this tutorial, it's not an immutable value class.  Making this class an immutable value
means just a couple of changes.  Adding the @ImmutableValue annotation and making the fields final.

```java
@ImmutableValue
public class Product {

	private final long sku;

	private final String description;
	
```	  

### Why Immutable Values?

So, why should immutability be related to pure programming?  The answer is that we want to write code which doesn't have side-effects.
One way in which side-effects can manifest themselves is in changing mutable state within the application.  A pure function should
take some parameters, do a calculation on them, and then return a value.  *Nothing in the system should get changed*. 

So, to meet this requirement, our pure methods will take immutable parameters.  

Now, before you throw away Pure4J in disgust, please know that *there are techniques to get around this restriction* and you can still build useful
code despite it, as we will see:   languages such as Clojure and Haskell *already work this way* and people get useful stuff done in them.

So, how do we change to make the class an immutable value?

Simply by:
* ensuring that the class has the `@ImmutableValue` annotation.
* It's fields are final
* It's fields are immutable too.

So, we change the class to look like this:

```java
@ImmutableValue
public class Product {

	private final long sku;

	private final String description;
```

We would set up these values in the constructor, like so:

```java
	public Product(long sku, String description) {
		super();
		this.sku = sku;
		this.description = description;
	}
```

... and we would get rid of the setters.  Job done.  Although, creating immutable values this way leads to rather a lot of IDE-generated
boilerplate code (for hashCode, equals and toString).   We can do better.  For our next value object we'll see how.

Checking Our Work
-----------------

What would happen if we left the setters on our `Product`, and forgot to make the fields `final`?  In that case, our class 
would not be as immutable as we'd hoped.   The point of Pure4J is that it can check and make sure you are getting it right.

Pure4J has a maven plugin which analyses compiled bytecode at build-time.   It looks for the annotations like `@ImmutableValue`
and checks that the [contract](http://robmoffat.github.io/pure4j/concordion/org/pure4j/test/checker/spec/Index.html) has been
met.  You get extra compile-time errors if it hasn't.

To activate this, add this to the pom:

```xml
...
<build>
	<plugins>
		...
		<plugin>
			<groupId>org.pure4j</groupId>
			<artifactId>pure4j-maven-plugin</artifactId>
			<version>*latest version*</version>
			<executions>
	         	<execution>
                    <goals>
                        <goal>pure4j</goal>
                    </goals>   
		         </execution>
		     </executions>
		</plugin>
```

Now when you run `mvn install` you will see the following entries in the maven log:

```
[INFO] Beginning Pure4J Analysis
[INFO] Marked Pure: org/pure4j/examples/tutorial/pure/Product.<init>(JLjava/lang/String;)V
[INFO] Marked Pure: org/pure4j/examples/tutorial/pure/Product.equals(Ljava/lang/Object;)Z
[INFO] Marked Pure: org/pure4j/examples/tutorial/pure/Product.getDescription()Ljava/lang/String;
[INFO] Marked Pure: org/pure4j/examples/tutorial/pure/Product.getSku()J
[INFO] Marked Pure: org/pure4j/examples/tutorial/pure/Product.hashCode()I
[INFO] Marked Pure: org/pure4j/examples/tutorial/pure/Product.toString()Ljava/lang/String;
[INFO] Finished Pure4J Analysis
```

So, we're all good.  Pure4J has scanned our `@ImmutableValue` class, and made sure that all of the methods on it were pure.  
If we had missed off the `final` from one of the fields, we would get this

```
[ERROR] Field private long org.pure4j.examples.tutorial.pure.Product.sku not declared final, but is immutable
```

And, if we had failed to implement `toString()`, say, we would get this:

```
[ERROR] Class org.pure4j.examples.tutorial.pure.Product was expecting a pure implementation, but uses inherited impure implementation
   See:[declaration=java/lang/Object.toString()Ljava/lang/String;
     impl=NOT_PURE
     intf=null, 
        usedIn=
       ]
```

Which tells us that we are using the `toString()` from object, which is impure.


The Basket
----------










