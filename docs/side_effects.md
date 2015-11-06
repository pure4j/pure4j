Side Effects And Immutability
=============================

If you are coming to pure programming for the first time, some of the restrictions enforced by Pure4J might seem
strange and perhaps arbitrary.  So here, we are going to establish exactly what this thing is, "pure programming", 
and what it means for the Java code you write using Pure4J.  

Remember, Pure4J is a compile-time *checker*:  it looks at the bytecode in your `.class` files, and checks that it 
adheres to certain rules, which, if followed, ensure pure functional java as a result.  So, let's start with 
what pure functions mean:

### The Definition Of Functional Purity

From [wikipedia](https://en.wikipedia.org/wiki/Pure_function):

* The function always evaluates the same result value given the same argument value(s). i.e. the results are deterministic.
* Evaluation of the result does not cause any semantically observable side effect or output. Such as output to I/O devices.

Let's take these one-by-one:

#### Deterministic

> The function always evaluates the same result value given the same argument value(s).

This means, that the return value of the function must depend *entirely* and *only* on the arguments passed into it.
That is, all the 'information' need to generate the output, is contained in the parameters.

* 'plus' is a good example (it takes two parameters): `6 + 6 = 12` -or- `plus(6, 6) -> 12 `
* Square (taking one):  `square(5) = 25`
* Uppercase (taking one parameter): `uppercase("hello") = "HELLO"`

These functions are deterministic, because they always return the same thing every time.  Here are two counter-examples of non-deterministic
functions:

* `System.currentTimeMillis() = ?` :  Always returns a different result, because it is based on the system clock, *not* on the parameters.
* `random.nextInt() = ?` : Again, based on some 'seed' value, held within the JVM, rather than a passed-in parameter.

What about reading from files?  Again, this is non-deterministic:

* `firstLineOf("somefile.txt") = ?` : You can't tell what this is going to be, just from the parameters.  It's the filename AND the contents
of the file that are going to determine the result, and the contents of the file have *not* been passed in as a parameter.

#### No Side Effects

Why is this important?   Why can we *not* read a file in a pure function?  Simply, because this would be a *side-effect* of the program,
and there are a whole load of ways in which this destroys the determinism that we are after:

* Someone could change the file either before, while or after we read it. So, the result could be *anything*.

> Evaluation of the result does not cause any semantically observable side effect or output. Such as output to I/O devices.

Now, before you throw your copy of Pure4J in the bin, consider that we're not saying *all* your code has to be pure.  *Obviously*, some
code will have side-effects.  That's fine.  [But as I argue here, there is a massive benefit to minimizing the code that does](forces.md).

But, we all know [Global Variables Are Bad](https://en.wikipedia.org/wiki/Global_variable#Use).  This is a well-documented fact in software 
engineering.  Global variables (and shared state generally) are exactly the kind of side-effect that functional programming aims to extinguish.

If our function used a global variable (reading a `static` value, say) it loses it's determinism:  it's output is *not* based entirely on the parameters, 
anymore.  The same is true of an object with mutable state:  if our function takes a mutable object as a parameter, then some other thread
could change that state *while we were using it*, and again, our results are non-deterministic.

It may seem surprising that you can even *do* useful work without shared, updateable state, but actually, you can, as can be seen in the 
[tutorials](tutorial1.md) [on](tutorial2.md) [this](tutorial_mutable_shared.md) [site](tutorial_collections.md).  

So, the use of immutable value objects really boils down to **determinism**:

* Pure functions are deterministic.  
* They work the same way *each time*.
* This makes them [*easy to test*](testing.md). 
* This makes them *thread safe*. 
* This makes them *scalable*.  
* This makes them *much easier to reason about*.
    
### Putting It All Together

* Pure4J provides simple annotations that allow you to build immutable classes ([@ImmutableValue](tutorial1.md)).  It allows you to test methods for purity.
Pure methods can only take immutable parameters.  This makes them thread-safe.  
* Pure4J also provides a way of managing state without sharing it ([@MutableUnshared](tutorial_mutable_unshared.md)).  State can be used (sparingly)
within the context of other pure methods.  
* Pure4J provides [collections](tutorial_collections.md) classes which guarantee immutability of both the collection and it's contents. This allows you to construct object graphs 
of arbitrary depth to model your data structures immutably.





