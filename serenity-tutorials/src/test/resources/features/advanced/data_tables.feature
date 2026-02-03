@advanced @data-tables
Feature: Data Table Handling
  Demonstrates horizontal tables, vertical tables,
  and custom DataTableType transformations.

  Background:
    Given the API is available

  @horizontal-table
  Scenario: Create order with multiple items using horizontal table
    Given an order with the following items:
      | item          | quantity | price  |
      | Laptop        | 1        | $999   |
      | Mouse         | 2        | $25    |
      | USB Cable     | 3        | $10    |
    When the order is processed
    Then the order should contain 3 items
    And the calculated order total should be $1079.00
    And the order status should be "PROCESSED"

  @vertical-table
  Scenario: Customer details using vertical table
    Given a customer with the following details:
      | First Name | John              |
      | Last Name  | Smith             |
      | Email      | john@example.com  |
      | Phone      | +1-555-123-4567   |
    And an order with the following items:
      | item      | quantity | price |
      | Keyboard  | 1        | $75   |
    When the order is processed
    Then the order status should be "PROCESSED"

  @calculated-totals
  Scenario: Order total calculated from items
    Given an order with the following items:
      | item       | quantity | price |
      | Monitor    | 2        | $300  |
      | Desk Chair | 1        | $250  |
    When the order is processed
    Then the calculated order total should be $850.00
