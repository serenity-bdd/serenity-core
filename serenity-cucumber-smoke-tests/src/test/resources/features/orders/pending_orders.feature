@billing
Feature: View pending orders

  Barry needs to see the orders that his customers have placed.

  Background:
    Given Sarah is a CaffeinateMe customer
    And Joe is a CaffeinateMe customer

  @smoketest
  Scenario: Barry sees all the orders
    Given Sarah has ordered an "espresso"
    Given Joe has ordered a "cappuccino"
    When Barry reviews the pending orders
    Then he should see the following orders:
      | Customer | Order      |
      | Sarah    | espresso   |
      | Joe      | cappuccino |

  Scenario: Cancelled orders should not be included in the pending orders
    Given Sarah has ordered an "espresso"
    Given Joe has ordered a "cappuccino"
    But Sarah cancels her order
    When Barry reviews the pending orders
    Then he should see the following orders:
      | Customer | Order      |
      | Joe      | cappuccino |


