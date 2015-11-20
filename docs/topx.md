9 Reasons To Start Writing Pure Functional Code In Java
-------------------------------------------------------

### 1.  Thread-Safety

Pure functions are by definition thread-safe.   This means you never have to worry about
locking, race-conditions or deadlocks.  This is a massive boon when thinking about your 
code.  

### 2.  No Dependencies

Joe Armstrong, the creator of Erlang, said:

> I think the lack of reusability comes in object-oriented languages, 
> not functional languages. Because the problem with object-oriented 
> languages is they’ve got all this implicit environment that they carry 
> around with them. You wanted a banana but what you got was a gorilla
> holding the banana and the entire jungle.

(See [John Cook's Blog Post](http://www.johndcook.com/blog/2011/07/19/you-wanted-banana/))

This feels true to me:  when I call a method, will it access a file on my system? Open a 
database connection?  Launch some missiles?  Without inspecting the code, I don't know.

By writing in a pure functional way, you guarantee your code is dependency-free: everything
the code needs to run is supplied as a parameter to the function.

### 3.  Determinism

Pure functions are *deterministic*.  This means that, for a given set of parameters, they give
the same result each time.  This makes your code much simpler to reason about.

### 4.  Easy-To-Test

A corollary of the "No Dependencies" advantage and the "Determinism" advantage is that code now 
becomes really easy to test:   you'll not need to write any more mocks, and your test fixtures 
will look a lot simpler.  

Also, the fewer dependencies you have, the more robust your code is.  By having no dependencies,
the chances are your tests will work nearly all the time, and you won't have to worry about colleagues
hitting them with the `@Ignore` stick.

### 5.  Reusability

Because you don't have dependencies, your code can be simply reused in any context. 

This is ideal when you want to run tests on a build server, check funcionality in a debugger, or deploy stuff into 
Hadoop and know it will work the same anywhere.

### 6.  No More Legacy

> Legacy code is source code that relates to a no-longer supported[citation needed] or manufactured operating system or other computer technology.

[Legacy Code, Wikipedia](https://en.wikipedia.org/wiki/Legacy_code)

Legacy code is code that is either poorly documented and tested, or that relies on archaic dependencies
outside the project.  Although alternatively, it could be *code without tests* (according to the same article above).

There's no way to predict whether a database will go out of favour, but 
at least if most of your code is pure and functional, you've isolated it from the legacy systems
and made sure it's reusable in any context.

Yes, your code is going to need Java.  But, you can isolate it from being legacy by:
* not having dependencies
* having great tests

... which, are natural outcomes from writing pure, functional code.

### 7.  Scalability For Free

Ok, so pure functional code is thread-safe, and there is no shared state.  This means you can scale to
your heart's content without running into issues.  Nice.

### 8.  No Hiesenbugs 

> In computer programming jargon, a heisenbug is a software bug that seems to disappear or alter its behavior when one attempts to study it.

From [Hiesenbug](https://en.wikipedia.org/wiki/Heisenbug)

Pure functions don't have *any* shared state, much less global state.  For the Clojure and Haskell guys,
this often means they build programs without a debugger, and just use a REPL.

Hiesenbugs are the bugs that, when you attach the debugger, don't always appear.  They're often due to obscure
race conditions or threading scenarios, and are a total nightmare to track down and reproduce.

Pure functional programmers have said goodbye to this problem completely.  

### 9.  You Don't Need To Learn A New Language

Sure, you could go and learn Haskell or Frege.  They are great.  But, you can get started on pure
functional programming within the JVM using Java [right now](https://github.com/robmoffat/pure4j/blob/master/README.md).  



