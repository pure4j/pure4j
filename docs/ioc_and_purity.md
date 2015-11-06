Coming From Inversion Of Control
================================

If you are having a hard time selling pure functional programming, (either to yourself, or your team) then perhaps 
something that is useful
is to compare it with [Inversion Of Control](https://en.wikipedia.org/wiki/Inversion_of_control) (or IoC).  

If you are working with Java, you've probably used [Spring](http://spring.io) Framework, because it's possibly
the most successful 3rd-party Java library available.  Even Pure4J uses it, and Pure4J is *tiny*.

Back in 2004, I remember coming across [Apache Avalon](http://www.theserverside.com/news/thread.tss?thread_id=26582) 
(and Excalibur) for the first time.  In a way, this project
was proto-Spring:  it was a framework designed around the concept of IoC.  However, in the end, Spring won out.  By
being a bit later to the game, it was able to fix a lot of the obvious mistakes of Avalon, and made IoC a lot easier to
understand and apply.

The point is, Inversion of Control was a hard pattern to understand straight away.  "Why should I not go looking for components?"...
"Why do I need a context to set everything up?"... "Isn't this just *harder*?" were obvious questions.

The reason it is such a huge runaway success is because, by doing IoC, you are making your code more **reusable**, less tightly coupled 
and **easier to test**.

Once you have seen these benefits, it's impossible to even *want* to go back to doing Java the old fashioned way of 
calling JNDI contexts and binding everything together in hierarchies of objects managing their own dependencies:  this kind of tight
coupling is just *wrong*. 

### Upshots

One result of IoC was that services exposed their dependencies via service interfaces.  For example, my `AddressBook` component
might need a `DataSource`, from which it could pull addresses using JDBC.   A `GameServer` might make use of a `PlayerRoster`.   

So components could be configured using the *real* implementations of the interfaces, or *mock* implementations.  Either these implementations
were written for the purpose of testing, or, they could be scripted up with a framework like [Mockito](http://site.mockito.org).

**Pure** Functional programming goes one step further:  by making it so that the only dependencies you have are explictly declared as parameters to the
methods, you can skip mocking entirely.  In the Haskell world, there is a testing framework called QuickCheck that can 
automate a lot of the testing effort involved in even writing tests.  (And this is an area for further investigation I think, in
terms of Java/Pure4J).

So pure functional Java really represents a next-level step in **reusability** and making things **easier to test**.

Plus, there are all the other benefits [I've written about](forces.md).  But, just like IoC, 
it's going to take a bit of getting used to and a bit of re-thinking your problems until the benefits are entirely clear.

### Conclusion

Pure functional programming probably doesn't have the same level of applicability of IoC (you'll likely need mutable state in your application)
but it confers further advantages along the same axes of reuse and testability as IoC.


