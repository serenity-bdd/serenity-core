@advanced @parameters
Feature: Custom Parameter Types
  Demonstrates custom Cucumber parameter types that transform
  step arguments into domain objects like dates, money, and roles.

  Background:
    Given the API is available

  @date-parameters
  Scenario: Schedule delivery using date parameter types
    Given an order placed on 2024-06-15
    When delivery is scheduled for tomorrow
    Then the delivery date should be after today

  @money-parameters
  Scenario: Apply discount using money and role parameter types
    Given an order placed on today
    And an order totaling $150.00
    When a manager applies a 10% discount
    Then the new total should be approximately $135.00

  @role-parameters
  Scenario: Admin can apply larger discounts
    Given an order placed on today
    And an order totaling $200.00
    When a admin applies a 25% discount
    Then the new total should be approximately $150.00

  @relative-dates
  Scenario Outline: Orders can use relative dates
    Given an order placed on <order_date>
    When delivery is scheduled for <delivery_date>
    Then the delivery date should be after <check_date>

    Examples:
      | order_date | delivery_date | check_date |
      | yesterday  | tomorrow      | today      |
      | today      | 2027-12-31    | today      |
