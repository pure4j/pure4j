Pure4J For Financial Calculations
=================================

Pure4J is born from experience in the financial services sector (specifically, risk). In 
this tutorial I am going to:
* Explain Value at Risk (as a simple financial calculation)
* Model an example of this on a spreadsheet
* Use Pure4J to implement the model in Java Code
* Use [Concordion](http://concordion.org) and the Excel spreadsheet example to build an automated functional test of the Java code.

We will cover each of these parts as we go along, and I will introduce all the concepts described
above.

But, the end result is that we will have:
* A pure functional, Java financial calculator, which can be embedded *anywhere we need it*.
* A Java functional test that demonstrates (and exercises) the calculator.
* Good code-coverage

### Introduction To Historic Value At Risk

Value at Risk (henceforth, VaR) is a methodology which aims to answer the following question:

|Given a particular confidence interval (say 95% certainty), and a particular period of time, 
|what amount of money should I expect to lose?

So, if you have a 1-day, 90% VaR of $500, you should expect that on average, once in twenty 
days you will lose more than $500.  

This is a really useful calculation for banks to have:  it allows them to establish confidence
intervals on likely losses, and give them some information about how much to hold back for a
rainy day.

Even if you only just  got the gist of what was written above, that should be fine for the purposes 
of understanding the rest of this tutorial.  If you want a longer explanation, then 
[take a look at Bionic Turtle's video](https://www.youtube.com/watch?v=yiyqIEWieEQ).  To keep things
easy to follow, I will be doing essentially the same example here.

In any case, this is *a simplified* example: we're trying not to let too many details creep in and ruin 
the big picture.  (On a specific note, our VaR will not consider currencies, or any period other than 
one day.)

### What About the 'Historic' Part?

The 'Historic' part of the VaR tells us how we are going to calculate our VaR.  Specifically, 
we are going to look at the historic gains and losses of (in this case) a 
portfolio of stocks (or shares), and, assuming the market will do in the future what it has done
in the past, work out our VaR from these historic changes.

In real life, the past performance of the stock market is no indication of what the future performance
would be.  For now though, we'll let that concern go:  this is the 'Historic VaR' because it's telling
us what the VaR would have been through a particular period of the past.

### Getting Started

We need four pieces of information to get started on VaR:

* The confidence interval (we are going to use 90%).
* The amount (in $) of each share in our portfolio.  This is called the *sensitivity* to that share.
* The historic movements (profits and losses) of some shares.  We are going to use 20 days' worth, and only 3 shares.
* The period over which we are measuring VaR (we are going to use 1 day).

Let's first model those first two items in Excel:

![Portfolio](tutorial_2_1.png)

For the historic movements, I downloaded the opening prices of the stocks from [Google Finance](https://www.google.com/finance),
and worked out the percentage profit / loss for each day, like so:

![Opening Prices To PnL](tutorial_2_2.png)

The graph of Profit / Loss looks like this:

![Graph Of PnL](tutorial_2_3.png)

So, the effect of converting opening prices to Profit / Loss essentially *normalizes* them all, meaning that the 
actual price of the share is irrelevant.  This means we can scale the profits/losses later to the size of our portfolio.

Now, no real VaR model would use *just 20 days* of PnL.  This is a *ridiculously short* time-frame, and there is a whole
art to choosing the time-frame to use, just on it's own.  20 days keeps the data size very manageable though, and 
hopefully means the screenshots will be clearer.

### The VaR Calculation

There are three steps to the calculation:

1.  Work out for the whole portfolio what the daily profit/loss would have been for each day, had we held it at that time.
2.  Order those profits and losses from lowest to highest.
3.  Take the result at the 90% point.  For our 20 days, this would be the second-worst result.

So, to do step 1, we need to multiply the holding of each share, by the profit / loss on that day.  So, let's do that:

![Profits And Losses Per Share](tutorial_2_4.png)

I've shown the formula in the screenshot - it's simply multiplying the size of the portfolio by the percentage profit / loss.

Next, I can calculate the overall portfolio profit / loss, simply by SUM'ing the columns:

![Portfolio Profit And Loss](tutorial_2_5.png)

If I sort these returns, and graph them, you can see the distribution of profits and losses over time:

![Profit And Loss Chart](tutorial_2_6.png)

If only all investing were this profitable!  Some really big gains and a few small losses.  

Let's add the formula for this: `=PERCENTILE(Table6[Portfolio], 1-B1)`, where B1 is the cell containing our confidence
interval.

Our VaR works out to be around USD -2.93, as point 3 on the graph is at the 90% confidence interval.  

### Creating A Pure Java Implementation

Having done this as an Excel spreadsheet, we can now go on to build this as a Java project.  There are going to be:

* A `Sensitivity` value-object, which will hold the amount of each stock.  e.g. USD 100 of MSFT.
* A `PnLStream` value-object, which will hold the profit-and-loss amounts over a time-range.
* A `VarProcessor` interface and it's implementation.

These are all available in the Pure4J-examples project, if you want to examine them in more detail.

#### `Sensitivity`

Looks something like this:

```java
public final class Sensitivity extends AbstractImmutableValue<Sensitivity> {

	final String ticker;

	final float amount;
	
	public Sensitivity(String ticker, float amount) {
		super();
		this.ticker = ticker;
		this.amount = amount;
	}
	
	public String getTicker() {
		return ticker;
	}

	public float getAmount() {
		return amount;
	}

	@Override
	protected void fields(Visitor v, Sensitivity s) {
		v.visit(ticker, s.ticker);
		v.visit(amount, s.amount);
	}
```

Note the use of `AbstractImmutableValue`:  this class declares the `@ImmutableValue` needed to tell Pure4J to check this class,
and it also contains the `equals()`, `hashCode()`, `toString()` and `compareTo()` methods.  In return, the implementation needs to
define the `fields()` method to say what fields the class has.  

#### `PnLStream` 

This is also a value object, so it starts like this:

```java
public class PnLStream extends AbstractImmutableValue<PnLStream> {

	final private IPersistentMap<LocalDate, Float> pnls;
	
	public IPersistentMap<LocalDate, Float> getPnls() {
		return pnls;
	}

	public PnLStream(IPersistentMap<LocalDate, Float> pnls) {
		super();
		this.pnls = pnls;
	}

	@Override
	protected void fields(Visitor v, PnLStream p) {
		v.visit(pnls, p.pnls);
	}

```

Since we are creating an immutable, value object, you can see that the `pnls` map contained within the stream uses
`IPersistentMap`.  This is an [immutable map implementation](tutorial_collections.md) available in Pure4J.

In order that we can do our VaR calculation, we need to be able to scale `PnLStream`s by a given `Sensitivity`, and
sum together a number of `PnLStream`s to get the position of our whole portfolio.  Let's look at those:

```java
	/**
	 * Merges two PnL Streams.  If "other" misses any dates present in this one, an exception is thrown.
	 * @param other
	 * @return A PnL stream the same length as the current one, with the same dates.
	 */
	public PnLStream add(PnLStream other) {
		PersistentHashMap<LocalDate, Float> added = PersistentHashMap.emptyMap();
		for (Entry<LocalDate, Float> entry : getPnls().entrySet()) {
			Float fo = other.getPnls().get(entry.getKey());
			if (fo == null) {
				throw new PnLStreamElementNotPresentException("For Date: "+entry.getKey());
			}
			
			added = added.assoc(entry.getKey(), fo + entry.getValue());
		}
		
		return new PnLStream(added);
	}
```

So, when you add one `PnLStream` to another, you get back the added `PnLStream`: there is no mutation of either of the 
original two streams.

Let's look at scaling the stream:

```java
	/**
	 * Scales up the PnLStream by factor f.
	 * @param f
	 * @return
	 */
	public PnLStream scale(float f) {
		PersistentHashMap<LocalDate, Float> added = PersistentHashMap.emptyMap();
		for (Entry<LocalDate, Float> entry : getPnls().entrySet()) {
			added = added.assoc(entry.getKey(), f * entry.getValue());
		}
		
		return new PnLStream(added);
	}
```

Again, scaling up creates a new `PnLStream` object which is returned.

#### The `VarProcessor`

Our interface looks like this:

```java
public interface VarProcessor {

	float getVar(IPersistentMap<String, PnLStream> historic, ISeq<Sensitivity> sensitivities);
}
```

Which is fairly self-explanatory.  Within our implementation, we set the VaR confidence interval:

```java
@ImmutableValue
public class VarProcessorImpl implements VarProcessor {
	
	private final float confidenceLevel;

	public VarProcessorImpl(float confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}
	
	
	
	
```

Again, we are constructing our Var processor as an immutable value object, but since I don't 
expect people to create collections of VarProcessors, I've not used `AbstractImmutableValue` here,
I've added the `@ImmutableValue` annotation instead.

#### `getVar()` Implementation

Let's work out the overall profit / loss stream for the portfolio.  
First, we multiply our sensitivities (the size of our portfolio) by the PnLStream's, and then 
sum them together, like so:

```java
public float getVar(IPersistentMap<String, PnLStream> historic, ISeq<Sensitivity> sensitivities) {
		// combine the sensitivities
		PnLStream combined = null;
		for (Sensitivity s : sensitivities) {
			String t = s.getTicker();
			PnLStream theStream = historic.get(t);
			float scale = s.getAmount();			
			PnLStream scaledStream = theStream.scale(scale);
			combined = (combined == null) ? scaledStream : combined.add(scaledStream);
		}
```

Having created the `combined`, scaled `PnLStream`, we can now do our percentile calculation on it:

```java
		// collect the results + sort (probably highly inefficient)
		Stream<Float> stream = combined.getPnls().values().stream();
		Collector<Float, List<Float>, IPersistentList<Float>> collector = PureCollectors.toPersistentList();
		IPersistentList<Float> results = stream.collect(collector);
		IPersistentVector<Float> sorted = PureCollections.sort(results.seq());
		
		// work out confidence level
		float members = sorted.size();
		float index = members * confidenceLevel;
		
		return sorted.get((int) index);
		
```

### Using Concordion To Build The Test

First, we have to declare the dependency on the Concordion Excel Extension. In Maven, this would be:

```xml
		<dependency>
			<groupId>org.concordion</groupId>
			<artifactId>concordion-excel-extension</artifactId>
	        <version>1.0.7</version> <!-- or whatever is the latest -->
	        <scope>test</scope>
		</dependency>
```

Then we can begin to define our test.  There are going to be two parts, the java class, and the Excel spreadsheet.
Our java class is called `ConcordionVarTest.java`, and our Excel spreadsheet must be called `ConcordionVarTest.xlsx`,
in the same package structure (but in the `src/test/resources`directory), like so:

![File Naming Conventions](tutorial_2_7.png)


`ConcordionVarTest` starts like this:

```java
@RunWith(ConcordionRunner.class)
@Extensions(ExcelExtension.class)
public class ConcordionVarTest {

}
```

These annotations instruct it that it's a concordion test, and to use the excel extension.  



