Pure4J Collections
==================

In order to create functions that are interface pure, or to create `@ImmutableValue` objects, you will often need to use collections.  However, the collections shipped
with Java are fundamentally mutable:  you can change the contents of them.  Even using `Collections.immutableList()` and it's ilk are not guaranteed immutable:  they
rely on an *underlying* collection, which may change, affecting the immutable version of the collection.

### Persistent Collections

To get around this problem, Pure4J utilizes modified versions of the [Clojure Persistent Collections](http://clojure.org/data_structures).  These collections classes 
are immutable once constructed.  Whilst the basic implementations were written by Rich Hickey (and credit goes to him, not me) I have adapted these in the following way:

* Adding back in support for Java Generics
* Tidying up the constructors available to be more like idiomatic Java.
* Simplifying the inheritance hierarchy of the interfaces
* Adding element checking, so that only immutable elements can be added to the collections (this prevents the collection's contents changing, ever).

|Persistent Collection|Original Java Collection|Implements|
|---------------------|------------------------|----------|
|PersistentHashMap    |HashMap                 |Map       |
|PersistentHashSet    |HashSet                 |Set       |
|PersistentList       |LinkedList              |List      |
|PersistentQueue      |LinkedList              |          |
|PersistentTreeSet    |TreeSet                 |Set       |
|PersistentTreeMap    |TreeMap                 |Map       |
|PersistentVector     |ArrayList               |List      |
|PersistentArrayMap   |*none*                  |Map       |

#### Basic Approach

All of the collections implement the usual Java interfaces.  However, the mutation methods will all throw a runtime exception if called.  Instead, new mutators are
included.  For example, `IPersistentCollection`s implement the following methods:

```java
@ImmutableValue
public interface IPersistentCollection<K> extends Seqable<K>, Collection<K>, Counted {

	IPersistentCollection<K> cons(K o);		// appends a new element to either the front or rear, depending on the collection type

	IPersistentCollection<K> empty();		// returns an empty collection
	
	ITransientCollection<K> asTransient();	
	
	IPersistentCollection<K> addAll(ISeq<? extends K> items);		// adds a number of new elements

}
```

So, for each mutation method, *a new collection is returned*.  This is the key observation to understanding how these new collections work.

The only one of these which is probably novel to most Java developers is the PersistentArrayMap.  This is suitable for storing very small maps (typically < 8 elements),
and stores them in an array.  Once it gets beyond a certain size, it will convert automatically to a PersistentHashMap instead.

### Guarantees:  Making Sure They Don't Change

In the Clojure world, objects are passed around as lists, which are essentially values.  But, in Java, we use objects.   This creates a problem:  

> What if I create a persistent collection, but the objects inside it are mutable?

That is, fields on the objects could change, which may alter their (`equals()`) identity or their `hashCode()`.  This is exactly what the `@ImmutableValue` annotation
is designed to solve.  Once you add this to your value objects (and add the Pure4J maven plugin), your objects will be compile-time checked to make sure they 
actually do fulfil the requirements for imutabillity.  That is:

* The fields on the class are `final`
* The fields are all also of immutable types (e.g. `int`, `String`, persistent collections or other `@ImmutableValue`s.
* There are no setters for the fields

Additionally, at runtime, when you construct your persistent collection, any objects you add will be checked for immutability.  This means you are guaranteed that 
the collection cannot change under you, breaking this contract.  (The one exception is, if code uses reflection: the type system is circumvented when you resort to 
this, unfortunately).


### Transient Collections

Each persistent collection class has a method to convert it back to a transient collection.  A Transient Collection:
* Wraps a standard java collection class
* Checks elements being added to make sure they are immutable.
* Has the `@MutableUnshared` annotation ([mutable unshared tutorial](tutorial_mutable_unshared.md)).
* Is implementation pure.

|Transient Collection|Wraps
|--------------------|--------------- 
|TransientHashMap    |HashMap
|TransientHashSet    |HashSet
|TransientList       |LinkedList
|TransientQueue      |LinkedList
|TransientTreeMap    |TreeMap 
|TransientTreeSet    |TreeSet
|TransientVector     |ArrayList

As with the persistent collections, there is a method `persistent()` which converts the transient implementation into a persistent one.  Transient collections are 
typically faster than persistent collections, if that's what you need.  Otherwise, you may as well use the persistent collections.


### `ISeq`s

By virtue of the fact that the collections implement the Java interfaces `Map`, `Set`, `Collection` and so on, they are also `Iterable`.  They will return `@MutableUnshared`
iterators which you can use within a pure function.   However, Clojure introduces the idea of an immutable iterator, through `ISeq` and `Seqable` interfaces.

An `ISeq` has the following methods:

```java
	K first();		// returns the first element of the seq

	ISeq<K> next();	// returns the rest of the seq, or null at the end
```

This provides an immutable way of iterating through a collection.  All of the persistent and transient collections implement `Seqable`, and `ISeq` also implements iterable, 
so you can use it with a standard `for` loop:

```java
	public void consume(ISeq<T> c) {
		for (T t : c) {
			something.add(t);
		}
	}
```

### Constructors

Broadly, there are four main ways to construct any persistent or transient collection:

##### Using an ISeq (pure)

Pass an ISeq into the constructor of a collection class to copy the collection.

e.g.

`new PersistentHashMap(<someISeq>)`

##### Interface Based (interface impure)

Pass an existing collection in (say a regular `HashMap`, `Collection` or `List`) to a constructor, and create a copy.

##### Array Based (interface impure)

Pass a varargs-style array to the constructor to initialise the contents via an array (not suitable for maps).

##### No-args (pure)

Creates an empty collection.

### A Note On Performance

For the persistent collections, which always construct new versions of themselves each time an object is added or removed, an obvious question is 
"Doesn't this destroy the performance of the JVM?".  

Good question.  Using immutable values changes the performance profile of the application.  But, does it make it better or worse? 

Certainly, adding a pair to a hash map involves more object construction than before.  There is likely to be more garbage collection.
But, on the other hand, you wont have to make defensive copies.  So, it's likely to be *a bit* worse.  

However, remember that these collections are taken from the Clojure world:  and high-throughput systems like Apache Storm are written
in that, so I would argue that often times, these collections are *good enough*.

Additionally, there are clever data structures being used here which aim to minimize the memory and CPU impact of the change.  So, adding a new
element to a 10,000 element array does *not* mean an entirely new copy of the array in memory:   adding and removing elements has something like
log N cost each time.



