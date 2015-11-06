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
of understanding the rest of this tutorial.

In any case, this is *a simplified* example: we're trying not to let too many details creep in and ruin 
the big picture.

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

* The historic movements (profits and losses) of some shares.  We are going to use 20 days' worth, and only 3 shares.
* The amount (in $) of each share in our portfolio.  This is called the *sensitivity* to that share.
* The period over which we are measuring VaR (we are going to use 1 day).
* The confidence interval (we are going to use 90%).

Let's first model those in Excel:

### The VaR Calculation

There are three steps to the calculation:

1.  Work out for the whole portfolio what the daily profit/loss would have been for each day, had we held it at that time.
2.  Order those profits and losses from lowest to highest.
3.  Take the result at the 90% point.  For our 20 days, this would be the second-worst result.

So, to do step 1, we need to multiply the holding of each share, by the profit / loss on that day.  Then, we sum
up over all the shares we hold, to get our overall profit / loss for that day.



