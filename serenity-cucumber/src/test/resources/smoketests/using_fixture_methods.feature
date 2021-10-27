Feature: Using Fixture Methods

  @start-at-two
  Scenario: Running a scenario with a Before clause
    Given I want to add two numbers
    When the first number is 1
    Then the running total should be 3

  @multiply-result-by-two
  Scenario: Running a scenario with an After clause
    Given I want to add two numbers
    When the first number is 1
    Then the running total should be 1
