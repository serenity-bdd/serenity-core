@basket
Feature: Shopping basket

  Background:
    Given an empty basket

  Scenario: Adding items
    When I add these items:
      | name  | price |
      | Tea   | 2.50  |
      | Bread | 1.20  |
    Then the basket total should be 3.70 dollars

  @wip
  Scenario: Work in progress
    When I add a "Cake" costing 4.00 dollars
    Then the basket total should be 4.00 dollars
