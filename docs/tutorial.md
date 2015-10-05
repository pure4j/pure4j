Pure4J Tutorial
===============

In this tutorial we are going to have a short introduction to building pure functions in Java, and checking that they're
pure with Pure4J.  For a description of why this might be a good idea, please review the [Why Pure4J](impetus.md) page, which 
discusses some of the problems this is trying to solve.

Scenario
--------

The use case we are going to consider here is for a calculator of a simple shopping basket, as follows:

1. The basket can contain various products.  It has a certain quanity for each product.
2. There is a price-list, which tells you how much each product costs (other price-lists may be used).
3. There is a fixed tax rate of 17.5%, which is calculated and added to the total of the basket.

I.e. this is an extremely simple calculation indeed!  So, understanding this calculation is not going to be the hard part.

Modelling
---------

* Each of our products will be modelled by a class (`Product`),  and each product will have a name and a SKU number. (Stock-Keeping Unit)
* We could either store the prices on each product, or have them looked up separately.  Since we want to be able to use a different price list some
times, we're going to hold the prices in a map.
* We'll have a class for our basket, and have a `price` method on it:  this will be our pure method. 

Product First
-------------

Let's start by modelling the product.





