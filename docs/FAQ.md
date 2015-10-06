Frequently Asked Questions
==========================

## What Are The Advantages of Writing Pure Code?

* Pure code is easier to test.  Because everything needed to perform the calculation is passed in to the pure method, you
can write deterministic tests and avoid bothering with mocking.
* Pure code runs anywhere.  I mean, Java runs anywhere, but this ensures that your code requires nothing more than a JVM:  it eliminates
dependencies on filesystem, network etc.
* Obviously, then, not all the code can be pure:  you will want to have "islands of purity" within your codebase which will
contain other, impure code too.
* Pure code is easier to reason about when threading needs to be considered.  In fact, pure code is thread-safe.

## What's the performance impact of the Collections Classes?

Good question.  Using immutable values changes the performance profile of the application.  But, does it make it better or worse? 

Certainly, adding a pair to a hash map involves more object construction than before.  There is likely to be more garbage collection.
But, on the other hand, you wont have to make defensive copies.  So, it's likely to be *a bit* worse.  

However, remember that these collections are taken from the Clojure world:  and high-throughput systems like Apache Storm are written
in that, so I would argue that often times, these collections are *good enough*.

## Why Does Everything Need to be Immutable?

Well, this isn't strictly true.  But, by limiting the place where an object can change state to *construction only* makes 
it much easier to reason about state.  If you're not sold on immutability, then consider the `String` class.  Yes, we could have 
mutable strings, but this is likely to make your code *much* harder to reason about.  Do you really want to go back to 
mutable strings?  Rich Hickey (the inventor of Clojure language) makes a similar, more persuasive case [here](https://youtu.be/-6BsiVyC1kM)

So, without immutability, it's very hard to come up with truly deterministic code.


## Is it really worth your while to bother with this, in a non-multi-threaded environment?

Probably yes.  A good analogy here is garbage collection.  In the C world, you had to manage your own memory, and people thought that
generally they could do a better job of this than an algorithm.  Usually, the programs ran fine, but every now and again something crazy 
might happen, which might be down to memory not being deallocated properly.  These are hard-to-track-down bugs.  Java took away
this opportunity for bugs to arise, and by and large people are happy not to have to worry about this in their coding again. 

The same goes for bugs in Java threading.  Although you might trust your own skills writing multi-threaded code, it's far easier to just
*not have to worry* and let the compiler  handle it for you.  Pure4J does this.

A lot of the time, Pure4J seems to be nit-picking:  "really, this needs to be final?  How is that *ever* going to bite me?"  But 
actually, each one of these tiny changes adds up to serious Heisenbugs being prevented over the lifespan of the project.

You could ask the same question about Java's static typing too:  When you start small, static typing *is* a burden.  But, if
you're building a big system (or a system you suspect will get bigger) then up-front efforts like purity and static typing do 
cost you in the short run, but pay back in the long run with increased correctness and eventually productivity.

## Why is <code>equals()</code> pure?

By this, if we are referring to the implementation on the `Object` class:

```
public boolean equals(Object obj) {
        return (this == obj);
    }
```

Then, it's pure as it has no side-effects, and is deterministic with respect to the two arguments (`this` and `obj`) that it 
is being called with.

## Why do I need to implement <code>hashCode()</code> and <code>toString()</code> in an <code>@ImmutableValue</code>?

Because if you do this:

```
new Object().hashCode();
```

the result of this method will be dependent on some state of your JVM. (Essentially, the hash of an object is based on it's 
memory address).   Therefore, this is non-deterministic. 

Since `toString()` uses the hash of the object in it's output, it is also non-deterministic.  Although actually, since 
`toString()` uses the hashCode, it should be ok just to override hashCode.  Hmm...

## How do I use Pure code with Dependency Injection (such as Spring)?

You can use the `@ImmutableValue` annotation on your spring classes. That will work.  However, if you do this, any dependencies
your class uses will need to be declared `final`, which means you will need to use Spring's constructor-based dependency injection.
Also, they will need to be `@ImmutableValue` classes too.

Apart from that, it should work fine.  

At some point, a new annotation might be introduced for spring beans, since these are not strictly 'value' objects, so you
shouldn't have to write `hashCode()` and `toString()` for them.

## How Does This Stack Up Against Mocking?

TBC.


## What is a Typical Use Case For This?

A good practice is to divide your code into different concerns.  For concerns where a calculation is being done, use Pure4J
to assert that the code is pure.   For other concerns, such as database access, interacting with the user, etc.  don't 
make your code pure.  

So, a typical use case is where you are doing complex calculations in Java for which the correctness of the calculation is 
a priority over developer speed.

## Is there a Plugin for &lt;favourite IDE&gt;?

Not yet. But, it would be nice if you wrote one.  :) Please [get in touch](mail:rob@kite9.com).

## How about a Gradle plugin?

Not yet.  Please [help](mail:rob@kite9.com).

## Why Can Pure Methods Only Take Immutable Parameters?

If pure methods took mutable parameters, it's possible that other threads could mutate the objects as they are being used.
This would make the pure method non-deterministic.  This is called *interface purity*.   It is not *always* required,
and private or protected methods within an `@ImmutableValue` class need not be interface-pure: they can be called by
other pure methods.

### Can I Use Arrays As Parameters?

TBD.  We might have a runtime check for this.

## How Do I Force Some Code To Be Pure?

Use the annotation `@Pure(Enforcement.FORCE)`.  However this is frowned upon - you are lying.  
You can make it pure by refactoring it.

## How Do I Make A Static Method Pure?

Add the `@Pure` annotation to the method.

## What About Using Threads?

TBD.  Should be ok, but I haven't written any test-cases yet, or checked the threading code for purity.  Same goes for 
the concurrency packages in Java.  Needs work.

## Why Can't My Pure, Immutable Object Have a (HashMap/LinkedList/ArrayList/etc) as a Field?

Simply, these classes are not immutable.  You can add an object into an `ArrayList`, or a `HashMap`, which breaks the 
immutability of the object.  Even if the map is constructed using `Collections.immutableMap()` there is no guarantee 
that the underlying map will not get changed at some point while your pure method is using it.  

This handy table shows the persistent version of each class:

|Persistent Collection|Original Java Collection|
|---------------------|------------------------|
|PersistentHashMap    |HashMap                 |
|PersistentHashSet    |HashSet                 |
|PersistentList       |LinkedList              |
|PersistentQueue      |HashSet                 |
|PersistentTreeSet    |TreeSet                 |
|PersistentTreeMap    |TreeMap                 |
|PersistentVector     |ArrayList               |
|PersistentArrayMap   |*none*                  |
|---------------------|------------------------|

## What Are These Transient Collection Classes (TransientHashMap, TransientArrayList etc.) ?

You can convert any persistent collection back into a transient (i.e. mutable) collection class.  
e.g. call `PersistentHashMap.transient()` to get a `TransientHashMap`.  

The transient versions have a `persistent()` method to turn them back into persistent collections.

They otherwise are `@MutableUnshared` collections, that will only collect immutable objects.
3
## Are Interfaces Pure?

You can mark an interface with `@ImmutableValue` or `@MutableUnshared` but only the concrete implementations will get
checked for purity.

*Interface Purity* means that the arguments of a method are all immutable values (either java built-in ones, or 
`@ImmutableValue`s.

## Are Generics Supported?

Yes.   For example, `PersistentHashSet` can only take immutable objects.  Otherwise, the set is mutable, no?  This is
tested at runtime, using the `Pure4J.immutable()` method, which will throw an exception if you try to populate the 
set with a object of a class which isn't immutable.

## Why is (Insert Some Method in the Java Standard Library) Not Pure?

This is either because a) it's genuinely not pure or b) it's not been tested.  Contact us on the group and get us to add
the check in the next version.

## Can I still use Java8 Streams?

Yes.  ISeq is stream-able.  e.g.

```java
	@Pure
	public ISeq<String> consumeBlah(ArraySeq<String> in) {
		ISeq<String> done = in.stream().filter((a) -> a.startsWith("e")).collect(PureCollectors.toSeq());
		return done;
	}
```

Note the use of `PureCollectors`.

## Can I Still Use Inner Classes?

Yes.  Obviously, static inner classes are just regular classes: you can do what you like with them.  Make them `@ImmutableValue` or 
`@MutableUnshared` or anything.  The interesting use-case comes with non-static inner classes.

The key thing to remember is that the compiler treats an inner class as a regular class, with privileged access to the outer class, a 
reference to which is passed in via the constructor (without you having to type it).

So, if you want an `@ImmutableValue` inner class, the outer class must also be `@ImmutableValue`.  You can have an `@MutableUnshared`
inner class too, again, only if the outer class is `@ImmutableValue`.



