Why Pure Programming In Java?
-----------------------------

Here I am going to try and address two questions:

* Why the hell would anyone want to use an imperative language for pure functional programming?
* Why would I want to restrict myself to functional techniques, when I could be using all that other stuff that
java allows?


### 1. You are working in Java Language environment.  

The first objection is, don't we have Clojure and Frege for pure functional programming?  If you aren't aware, 
[Clojure is a Lisp](http://clojure.org) and [Frege is a Haskell](https://github.com/Frege/frege).  Both of which are 
excellent.  If you are in the business of wanting to learn a new way of programming, but also committed to the
interoperability that the JVM provides, then I definitely recommend checking these out.

But, it isn't always possible to decide on a new language and just adopt it.  

If you think about it, this is why  lambdas (and generics before it) have been added to Java:  sometimes we need to work within the tools we have, 
and, if that's not enough, adapt the tools we have to our own needs.  

In the business I work in (Finance), we have *huge* mental investment in Java.   We know that graduates arriving here
will also know Java.  We know that we can hire pools of talent that also know Java around the world.   

Is this a perfect situation?  

No.  

Are there better languages?  

Possibly.   

But, the fact that *so many* people have standardized on *this one* language is nothing short of a miracle in an environment where everyone likes to download
and use a different NoSQL database for *every single project* just for the hell of it.  

So, to introduce the ideas of *pure functions*, *programming with immutability* and *value based programming* the simplest,
lowest-cost way to do this is to *stick with Java*.

And, sometimes, unilaterally introducing a new language into an otherwise homogenous codebase just looks like CV building, and
is likely to *piss everyone off* that has to maintain it after you left.  

I.e. sometimes being at the cutting edge of language design is a un-3pragmatic disaster.  

### 2. Code correctness is a primary concern.

That's not to say that there isn't an overhead in using functional techniques.  

Here are two:

* You're going to be designing a lot more *immutable objects* (with the `@ImmutableValue` annotation) than before.  
Pure4J will ensure that your classes meet the criteria of immutability when they have this annotation.
* You're also going to need to get used to using [persistent collections](tutorial_collections.md) (and remembering to keep 
a reference to the returned collection when it comes back).  

This seems like an overhead, but by doing this, you are now doing *value based programming*, and you free yourself 
from ever having to worry about global variables changing on you, or side effects from the code you're running, or 
synchronization around a mutable value.

This is going to seem like an unfair trade-off, at least at first.

Yes, you are being restricted in a lot of things that you *used to be able to do*, with the promise of fewer bugs *in the future*.  

But there are likely to be *at least some* parts of your code-base where that's a good deal:  
usually in maths-heavy parts, or bits of code where it's hard to reason about exactly what is happening behind the scenes.

### 3.  You Want to Ensure Your Code is Testable.

So, Pure programming and immutability are desireable for certain sections of the code-base.  

So called "Islands Of Purity".  

By writing in a pure style, you're going to make testing *so much* easier:

* Because pure functions *don't have side-effects*, you won't need to handle complex sets of mock objects to enable you to write tests:
everything needed to perform a calculation is provided as an argument to it.
* Because you are working with code that is *deterministic*, you can *always expect the same result* from an interaction.

### 4.  You Want to be Able to Reuse Your Code in Other Contexts, and Know That it is Dependency-Free.

Where I work, we have lots of "strategies".   

Every week, there is a new architecture diagram.  Pinned to the wall.  Maybe framed.  In a well-meaning power-point presentation.

What if you could just *stop worrying about all of that*?  

If you knew, no matter what the strategy was, your code was *strategy proof*?

This can happen.   

By doing pure functional programming, you are saying:  no side effects.   Everything I need is going to
be provided as an argument to the function.  I'm not going to go off and call a database.   I'm not going to 
talk to a message bus.   This code will have the data from those things provided to it.  

Now obviously, *your whole code base* can't work like this.  Something has to deal with databases.  And message buses.
But, your *pure* code?  That can be strategy-proof.  And, it's in your interests to make as much of it like this as possible.

This also explains why we can get rid of all that mocking:  no side-effects means no dependencies means no services to mock.

### So to summarize:

|Pain Now         |Less Pain Later 
|-----------------|----------------
|Immutable Objects|No Global State / Heisenbugs
|Pure Methods     |No Synchronization Issues
|Determinism      |No Side-Effects
|                 |Composability
|                 |No Dependencies
|                 |Easy Testing
|                 |Easy Scalability

The more I write about this, in the industry I work in, the more convinced I am that this is the right way 
forward for a lot of code being written in Java today.  

I hope you find this useful too.  Let me know what you think.  rob -at- kite9.com



