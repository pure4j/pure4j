Design
======

This doc basically contains a distilled set of all the design notes I made for Pure4J, originally held in Evernote.
I'm moving them here so that they are more publically available.

### The Purity Algorithm

#### Step 1: Building the Project Model

* For each constructor / method, store what it calls in terms of:
  * other methods (on objects), fields (these are MethodHandles and ConstructorHandles).
* Store a “pure list” of methods either annotated with @Pure, or who’s class is annotated with @Pure.  We need to know the methods
* method -> Set[methods/fields]  Use the handles.
* This is done with ASM bytecode analysis, and the results are held in `ProjectModel`s.

#### Step 2: IsImmutableValue / isMutableUnshared

For each class, you need to decide if it is an ImmutableValue.  This is an easier proposition, because you just need to 
look at the inheritance hierarchy.  So, for each class, check:

* class -> interfaces
* class -> superclass
* class -> annotations  (all these are available on Class<>)
* produces `ImmutableValueClassHandler.typeIsMarked(someclassname)`  (keep a cache)
  * Class -> `ImmutableValue` map.

* `ImmutableValueClassHandler.doClassChecks()`: Make sure all immutable field values are final in concrete classes.

Mutable Unshared works the same way:
* `MutableUnsharedClassHandler.typeIsMarked()`
* `MutableUnsharedClassHandler.doClassChecks()` : Make sure all mutable state is private

#### Step 3: Complete The pure list

* For each ImmutableValue/MutableUnshared class, work through it’s declared methods & constructors.  
* Add them to the pure list.

#### Step 4: Check Purelist Purity

* Purelist now contains everything we think should be pure.  
* For each pure function, check that it is calling only other pure functions, and getting fields from immutable values. 
* There are differences between the checks for ImmutableValues and MutableUnshared.  (See later)
* For each function in purelist that doesn’t make the grade, report an error.

#### Step 5:  Construction of ImmutableValues / Method Calls On ImmutableValues

* Sometimes, we are going to have ImmutableValue classes created outside of the current project.  
* But, if we have something using one of those ImmutableValues, they could smuggle something mutable in inside it.  So, we have to protect against this.
* For each call to an ImmutableValue constructor, we need to ensure that the element being inserted is also Immutable.  How?
	* Possibility 1:  The constructor only takes known-to-be immutable items.
	* Possibility 2:  The constructor is generic, and can take pretty much anything
	* Possibility 3:  The constructor takes object.

One way to be sure is to find all the calls to this constructor, and make sure that in every case the correct object type is being passed.   
This is not going to fly if it’s being used as a library, but, on the other hand, it’s not a disaster either.

A second way is to add some code to the class to ensure that the constructor is invoked with immutable arguments.   
This way is a disaster.  Eclipse would hate us.  Let's rule out byte-code modification, because it makes things harder to debug. (a la Lombok)

A third way is to get the user to add some code to the class.  This actually might be the best way.  Additionally we can then check that this is called 
for each parameter being passed into the constructor.  (This was done, using `Pure4J.immutable()` to guard all parameters of mutable types.

A fourth way is to make it so that ImmutableValues have an interface to subclass.  However, this is not likely to be great either - lots of libraries
(for example, collections) depend on being able to take `Object` as the element type, due to generic type erasure.

### Interface Purity and Implementation Purity

Often, a method will be passed mutable arguments, but be otherwise implementation pure (i.e. deterministic, no side-effects).  
This could be a problem, say if you had a constructor that takes an array.  What if the array changed, during construction?
On the other hand, if we have a pure function calling something that is "interface impure" that's not going to be a problem.
So, for public methods, we also check the interface purity (i.e that the the arguments are immutable).

### MutableUnshared:  It's Use Case

1.  Visitor example: I can only pass the visitor into the function if it is @ImmutableValue.   However, that’s not right.  
I should be able to pass in a pure function, that I can compose with.  This is kind of the essential point of Lambda calculus.  
This is a real problem, right?  So, this is something similar to an @ImmutableValue, but perhaps not the same.  Certainly, it needs to be something @Pure.

2.  Callback example: For the callback, I am similarly in trouble:  I have to check that the value being passed in is Pure4J.immutable, otherwise my pure function is 
no longer pure.  Actually, this is ok, generally you will be calling back on value objects anyway.   Solved

Essentially, we want to be able to have objects with state, but know that their state is guarded against corruption by the side effects of other methods.

Simplest example is the counter.

The simplest way to envisage this is to say that a pure method on a pure class is allowed to change the state of the object itself.    i.e. that’s it’s one allowed side-effect.  And, the state must be private/protected to the class.  Essentially, this means that you have some extra arguments to the method you are calling, which are held in the object.

* A pure method can only return immutable values.   is this necessary?
* A pure method can only set immutable values (on its class).  We do this by checking the field types.
* A pure method can only take immutable or pure parameters (we check them as we go).

By using a couple of different pure objects, can you extract the mutable state of a pure object and allow it to be shared (therefore causing side-effects)?  
This is the central question.  So, the mutable unshared must 'guard' against this at all costs.

#### The Difference


##### @ImmutableValue Objects

* Surely this means that methods/constructors on an immutable object can be passed parameters of any type.  The reason we have resisted this before is that the parameters might not be thread-safe.
* There is no way for the immutable object to record those parameters, unless it creates new immutable objects.
* However, if it does this, the contract is that it must be defensive.  i.e. A for a new immutable object to be created, all the fields on it must be final and immutable.  Array coming in?  Vector being recorded.
* So, my contract for methods on immutable classes is too strict, in terms of what arguments the methods should allow.

##### @MutableUnshared, Unshared Objects

* Are allowed to have mutable state within them.
* We only allow it to take parameters which are immutable (guarding against taking other’s state).
* We only allow it to return values that are immutable (guarding against sharing own state).
* State on the mutable object should be guarded (i.e. not public).
* We don’t want two mutable objects from different owners to ever come into contact with one another:  this is how state gets mixed. (They can’t if we follow the above rules)

### Equals / HashCode

* Immutable classes need to implement equals themselves.  (or, do they?)
* We want to provide some convenience methods around equals to make life easier.
* Java primitive wrapper classes all implement equals correctly.
* My collections classes currently use Object.equals.  Is this wrong?

#### HashCode is not Pure, But Equals Is

So, this is crazy, but actually equals is pure.  Because it’s either doing value-equals, or identity-equals.  Both of which are fine.

So, maybe we don’t even need an equals story.  So to summarise:

* I have marked `Object.equals` as pure.  Because, it is - it just compares using ==.
* I have allowed the equals method to not  have it’s parameters checked for immutability.  The contract of equals is not to change stuff anyway.

But.

* We are requiring people to implement hashCode.  And so, they will definitely have to implement equals.  
* If they use the EqualsBuilder, then we are going to need to make it pure.

So, this argues for including (something like) EqualsBuilder in our code, probably, as a pure function. (@see `Pure4J.equals()`)

#### Calling hashCode is always fine

So, if we have a pure function, we are going to check objects are immutable.
If we check that objects are immutable, we know that they will have implemented their own hashCode.
If they do that, the hashCode will be pure.

* calling hashCode is pure.
* Any method called on a parameter is going to be pure, because all ImmutableValue methods are pure.
* It’s possible to have an impure base implementation, and pure subclasses.  If we’re calling on an ImmutableValue, it’s pure.

### Enforcing Purity

These two tables cover the way in which we enforce the purity contracts.  Often, we have to do something
that is *more restrictive* than it really needs to be to ensure purity.  I've called this 'overzealous'
behaviour, and it would be good if we could achieve the thing on the left without such a strict thing on the
right.

#### Method Determinism (public) - ImmutableValue -or- method=@Pure

|Requirement            |Implemented By
|-----------------------|--------------
|You *can* set fields on the return object / intermediate objects.|No need to check
|You *can’t* set fields on the parameter objects (side-effects)|All arguments must be immutable  (overzealous).  Alternative: None really, because you can’t tell the difference between a parameter and an owned object.
|*fields* should not change. (thread-safety) |All arguments must be immutable (overzealous)
|You *can’t* set any fields on the ‘this' object (side-effects)|Methods having to be on an immutable object, or static, or not using ‘this’. (overzealous) Alternative: None, really, due to not being able to tell one class instance from another.
|You *can* call other method-deterministic methods|Having a pure-list
|You *can* create and use class-deterministic objects.|Checking Pure List (Constructors)
|You can create arrays, and set them within the method.|No need to check
|You can’t change arrays passed to you(side-effect)|Arguments must be immutable, therefore not allowed to pass arrays, unless we defensive copy them. (a touch overzealous)
|You can’t access arrays passed to you (they may change)|Arguments must be immutable, therefore not allowed to pass arrays. (a touch overzealous)

#### Lifetime Determinism - MutableUnshared

|Requirement            |Implemented By
|-----------------------|--------------
|You *can* set fields on the return object / intermediate objects.|No need to check
|You *can't* export your state in a return object|Check return type -or- `Pure4J.returnImmutable`
|You can’t set fields on the parameter objects|All arguments must be immutable  (overzealous)
|*fields* should not change. (thread-safety) |All arguments must be immutable (overzealous)
|You *can* set fields on the ‘this’ object (including arrays)|No need to check
|You *can* call method-deterministic methods|Pure list checking
|You can create and use class-deterministic objects.|Pure list checking (constructors)
|You can create arrays, and set them within the method.|No need to check
|You can’t change arrays passed to you(side-effect)|Arguments must be immutable, therefore not allowed to pass arrays, unless we defensive copy them. (a touch overzealous)
|You can’t access arrays passed to you (they may change)|Arguments must be immutable, therefore not allowed to pass arrays. (a touch overzealous)

### `toString`

`Object.toString` is actually pure in implementation, so long as you override hashCode.  Since we are forcing every ImmutableValue to do this 
(since hashCode is impure) we should actually make the default toString implementation pure anyway.
