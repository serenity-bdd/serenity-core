@singlebrowser @resetappstate @tax
Feature: Completing a purchase

  Background:
    Given Colin has logged onto the application

  Rule: Customers must provide their name and address during checkout

    Scenario Outline: Colin enters incomplete name and address info
      Given Colin is browsing the product catalog
      And Colin has selected an item and checked out his cart
      When he provides the following personal details:
        | First Name   | Last Name   | Zip/Post Code   |
        | <First Name> | <Last Name> | <Zip/Post Code> |
      Then he should be presented with the error message <Error Message>
      Examples: Some examples
        | First Name | Last Name | Zip/Post Code | Error Message                  |
        |            | Collector | ABC-123       | Error: First Name is required  |
        | Colin      |           | ABC-123       | Error: Last Name is required   |
        | Colin      | Collector |               | Error: Postal Code is required |


  Rule: Customers should see a summary of their order before they complete their purchase
    Example: Colin purchases two items and sees them both appear in the purchase summary
      Given Colin has the following items in his cart:
        | Sauce Labs Backpack     |
        | Sauce Labs Bolt T-Shirt |
      When he checks out his cart providing his personal details
      Then he should be presented with a summary of his purchase including:
        | Qty | Description             | Price  |
        | 1   | Sauce Labs Backpack     | $29.99 |
        | 1   | Sauce Labs Bolt T-Shirt | $15.99 |
      And the total price should be:
        | Item total | Tax   | Total  |
        | $45.98     | $3.68 | $49.66 |

  Rule: Customers should be informed that their order has been placed
    Example: Colin confirms his order and is told that the items have been dispatched
      Given Colin has the following item in his cart:
        | Sauce Labs Backpack     |
        | Sauce Labs Bolt T-Shirt |
      When he checks out his cart providing his personal details
      And he confirms his order
      Then he should be informed "Your order has been dispatched"
