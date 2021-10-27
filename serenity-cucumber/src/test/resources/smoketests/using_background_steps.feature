Feature: Using Background Steps

  Background:
    Given I want to add two numbers

  Scenario: Skipping a scenario
    When the first number is 1
    Then the running total should be 1

  Scenario: Running a scenario
    When the first number is 1
    Then the running total should be 1
