@singlebrowser
@catalog
Feature: Filtering products

  Background:
    Given Colin has logged onto the application

  Rule: Customers should be able to sort product details to see the products they are most interested in
    Scenario Outline: Sort products
      Given Colin is browsing the product catalog
      When he filters the products by <Sort Order>
      Then the first product displayed should be <First Product>
      Examples: Sort by price
        | Sort Order          | First Product                     |
        | Price (low to high) | Sauce Labs Onesie                 |
        | Price (high to low) | Sauce Labs Fleece Jacket          |
      Examples: Sort by name
        | Sort Order          | First Product                     |
        | Name (A to Z)       | Sauce Labs Backpack               |
        | Name (Z to A)       | Test.allTheThings() T-Shirt (Red) |




