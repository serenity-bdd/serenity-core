@billing @sprint-1
Feature: Order a coffee

  In order to save time when I pick up my morning coffee
  As a coffee love
  I want to be able to order my coffee in advance

  Background:
    Given Cathy is a CaffeinateMe customer

  Rule:  Buyers can specify their preferences when they order
    Example: Buyers can add a comment with their order
      When Cathy orders a "large cappuccino" with a comment "Double sugar"
      Then Barry should receive the order
      And the order should have the comment "Double sugar"

  Rule: Buyers can order many items in the same order
    Example: A buyer orders two items in the same order
      When Cathy places an order for the following items:
        | Product          | Quantity |
        | Large cappuccino | 1        |
        | Espresso         | 2        |
      Then Barry should receive the order
      And the order should contain 2 line items
      And the order should contain the following products:
        | Large cappuccino |
        | Espresso         |
