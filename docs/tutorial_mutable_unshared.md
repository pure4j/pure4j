Mutable Unshared - Tutorial
---------------------------

### Overview

The bread-and-butter of Pure4J is the creation of `@ImmutableValue` objects, and passing these between pure functions to 
create flow-based systems that can easily be reasoned about.  However, you will definitely run into cases where this is not
sufficient, and you need to hold state.

In Pure4J, objects tagged with `@MutableUnshared` contain state.   This name
comes from the fact that there are two properties:
* Mutable:  the object contains some state which can be changed. Perhaps through setter methods.
* Unshared: the state of the object is wholly owned within it, and will only be accessible and changeable through those setter
methods.

`@MutableUnshared objects are still deterministic.  However, while `@ImmutableValue` objects are deterministic on a per-method basis,
`@MutableUnshared` ones are deterministic over the entire lifespan of the object.  

Methods on `@MutableUnshared` are checked for purity in the same way as `@ImmutableValue` objects.  They cannot call side-effecting
methods on other objects, though they can change their own state.  

#### Parameters Must Be Immutable

In order to prevent `@MutableUnshared` objects from causing side-effects, the parameters to their methods, must be immutable.  This is
 a touch "overzealous".  It would be nice if we could allow a less restrictive interface, but unfortunately
anything less would allow us to share state:   For example, if you could pass a map into the `@MutableUnshared` (to be stored as part of
the state), then any actor with access to that map could change it, breaking the determinism of the `@MutableUnshared` object.

#### Return Values Must Be Immutable

For much the same reason, the return value of any method on a `@MutableUnshared` must also be immutable.  Otherwise again, state could 'leak' 
out of the system.  This means that you're only ever going to return 'views' of the state inside the `@MutableUnshared` object.

[Full specifications are here](http://robmoffat.github.io/pure4j/concordion/org/pure4j/test/checker/spec/Index.html).

### Example

Let's write a very simple example, in which we count the number of occurrences of characters within strings fed to the program.
After receiving a number of strings, we return a map which contains the histogram of character-counts. (This is from the pure4j-examples project)

```java
@MutableUnshared
public class CharacterHistogram {

	private TransientHashMap<Character, Integer> counts = new TransientHashMap<Character, Integer>();
	
	public void countCharacters(String in) {
		for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			Integer count = counts.get(c);
			if (count == null) {
				counts.put(c, 1);
			} else {
				counts.put(c,1 + count);
			}
			
		}
	}
}
```

Some things to note:
*Our state, `counts` is private.  If it were public or package access, we would need to make it immutable so that nothing from "outside" the
class could change it.
* We can call the method `countCharacters()` over and over again, and the histogram stored in `counts` will update each time.

#### Returning The State

Now, as mentioned earlier, it's not possible to simply return mutable state from the `CharacterHistogram`:

```java
	public Map<Character, Integer> getCounts() {
		return counts;
	} 
```

Would return the following error:

```
[ERROR] method can't return non-immutable type on line: 27
   See:[declaration=org/pure4j/examples/tutorial_mutable_unshared/CharacterHistogram.getCounts()Ljava/util/Map;
```

Obviously, if we returned this map, *anyone* could change it without interfacing with `CharacterHistogram`, and the determinism would be lost.  Instead we need
to return an immutable 'view' on the state like this:

```java
	public IPersistentMap<Character, Integer> getCounts() {
		return new PersistentHashMap<Character, Integer>(counts);
	}
```

A client receiving this object back can *try to* make any change they like, but `counts` within the `CharacterHistogram` will not change.  

Hence, state is 'owned' by the `@MutableUnshared` object in a very controlled way.

### Return Types

One of the main uses for this within Pure4J is for iterators.   Pure4J in fact defines `IPureIterator` and `IPureListIterator` for this purpose, and the collection
classes return instances of this interface when you call `iterator()` on a collection.

Each time you call `next()` on the iterator, the internal state of the iterator changes. 

This is the interface of Iterator:

```java
public interface Iterator<E> {

    boolean hasNext();

    E next();
```

However, there is a problem here: `E` could be a mutable object, and, the contract is that we don't return 
any mutable state from a mutable unshared object.

Often, you can narrow the return type like this:

```java
	public SomeImmutableObject next() {
		return someImmutableObject;
	}
```

This is fine.  If you don't know what the method will return until runtime, you can check it before returning 
like this:

```java
	public Object next() {
		return Pure4J.returnImmutable(someImmutableObject);
	}
```

If you have this check on all the return paths of the code, then the purity check will pass.

### Thread-Safety

By default, `@MutableUnshared` objects are not thread-safe, because they contain state. (`@ImmutableValue` objects,
conversely, *are* safe, because they contain no state).  

Given that the determinism guarantee *spans mulitple method calls*, it is going to be important to either
ensure each object is accessed by a single thread, or that you synchronize access to the object.

