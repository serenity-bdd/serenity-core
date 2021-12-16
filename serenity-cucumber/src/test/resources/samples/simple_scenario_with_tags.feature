@flavor:strawberry
Feature: A simple feature with tags
  This is about selling widgets
  @shouldPass
  @color:red
  @in-progress
  Scenario: A simple scenario with tags
    Given I want to purchase 2 widgets
    And a widget costs $5
    When I buy the widgets
    Then I should be billed $10

