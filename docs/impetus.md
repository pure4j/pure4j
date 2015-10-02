# Why Pure4J?

My introduction to pure programming goes something like this:   I started off looking at Scala a few years ago, trying to 
decide whether it was worth all the hype.   I bought some books and did some research, but something about Scala (at the time)
didn’t feel right.  At the time (and still) I am working for an investment bank, in London.  The exact one is unimportant, 
this applies to all of them (I’ve worked in several, I have friends in many others).  We have a wide range of ability levels 
amongst the staff and sadly, a lot of the code is very low quality.  One of my jobs in the bank was to try and get a grip on 
the automated testing for a software system.  I’ve written before about automated testing, and why it should now be essential, 
but the truth is, this is still an area in which we desperately need to see some improvements.

Anyway, the idea of using Scala filled me with some dread:  our Java code was hard enough to reason about.  The wide buffet 
of abstractions available to Scala programmers, plus features like operator overloading made it feel like, we would be 
creating a nightmare rather than a utopia.  Put simply, there was just too much scope to screw everything up.

I decided: If you spend more time reading code than writing it, Scala is a bad deal.  I don’t know whether this is 
still the case.  I have a feeling that it is.  If Scala wants to provide the best-of-both-worlds FP plus Java 
backwards compatibility, it is taking on a huge amount of essential complexity.  And, I think, this is why 
Martin Odersky does presentations like [this](https://www.youtube.com/watch?v=WxyyJyB_Ssc).

## Next: Clojure

I then had a brief play with Clojure, and wrote a software project in it.  Clojure is good.  It is an amazingly well-designed 
language, and was pretty simple to learn.  However, as a lisp, it doesn’t fit with my idea of software engineering.   
I like that Java provides strong, compile-time type checking, and I like to be able to refactor using it.  My belief is this: 
If I am working on a collaborative project with a large team of developers of varying skill levels, then strong typing is 
much more of a help than a hindrance.

One thing that did come out of this was an understanding of Immutable (or Value-based) programming.  Rich Hickey 
is [a strong advocate of this](https://youtu.be/-6BsiVyC1kM), and it 
[underpins the whole of Clojure](http://www.infoq.com/articles/in-depth-look-clojure-collections).  

## Frege / Haskell

So, banks are pretty wedded to the JVM.   We have a lot of legacy code that we want to keep running.  But, did you 
know that they’ve [implemented a Haskell on the JVM](https://github.com/Frege/frege)?  Frege seems like the perfect 
solution for me:   strong typing, pure functional programming.  It’s well worth a look, check it out!

What does Haskell provide that I like?  Pure Functions.  

The simplest pure function I can think of right now is "plus".  i.e:  `5 + 5 = 10`.  Or,

```
plus(5, 5) -> 10
```

Plus works the same everywhere.  It doesn’t matter what day of the week you run it on.  It doesn’t go off and query databases. 
To test it, I don’t need to mock a plusServer.  When I do plus, nothing else in my program changes.  This is the definition 
of purity.  Frege gives you this, but for any functions you want to write.   Whether they are “plus” or “calculate the risk 
on these financial instruments”.

But, there’s still a problem here:  the developers I work with are not going to take kindly to me committing Haskell code 
into our projects.   Maybe, one day this will become the norm, and that sounds perfect.  But right now, it’s just not a 
thought anyone is going to entertain.

What I picked up from Frege was this though:  

1.  They allow you to mark certain methods in the JVM as pure, and use them from within Frege code.    
2.  The concept of Purity Islands:   parts of your code that you want to be pure, as compared to the parts of your code that deal with IO and shuffling the data around.  

You should be able to have a system containing both types of code and reason about them separately.

## Strategy

A final force is "strategy".   Banks like to be able to write down where they want their IT strategy to go.   Such as:
* What is the strategic direction for X? 
* What is the strategic location for this piece of data? 
* In the future, strategically all the applications will conform to some interface X.

This is not really strategy in the sense that an MBA might understand it.  This is just a will to exert ones ideas on the
world, for better or worse.  Often the reasons why /we need a strategy/ is one of appealing to budget-holders.  But more 
commonly, it's about managers wanting to be able to do design.  

The problem is, writing a strategy is easy.  You can do it on powerpoint.   A few weeks pass, a new manager arrives, 
and there is a new strategy.   As I say, this is a common problem amongst all the investment banks I touch.

But, writing code as pure functions takes you some of the way there:  /Pure functions can run anywhere./  Because they 
don’t have side-effects, they don’t have dependencies on other systems.   This is immense:  

> Irrespective of the strategy of the month, we can run our code anywhere there is a JVM:  Hadoop, Spark, Coherence, whatever.  

## Pure programming is strategy-proofing.

Putting It All Together

So for now, it would be nice if you could:

Read and Write Code In Java (as a lingua franca)
Make It Testable
Make it Deterministic
Make it Strategy-Proof

Obviously, it would be nice if we could do functional programming on the JVM.  And actually, Java8 is a step in the right direction.  I am still undecided on whether it will make my life easier or not (as I haven’t seen much abuse of it in the wild yet).  But, I can live without this.

Pure4J

Frege proves that pure programming is possible on the JVM.  Clojure proves you can have immutable (persistent) collections and still have a language.  All that is needed is to bring these two ideas together within plain Java and we can hit all of the bullet points above.  

Pure4J gives you the tools to specify (and then check at compile time) immutable classes.  It also gives you the tool to identify your islands of purity, and again, check that this constraint is met at compile time.   
