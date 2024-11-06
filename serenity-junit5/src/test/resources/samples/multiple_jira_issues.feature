@foo
@issues:ISSUE-123,ISSUE-789
Feature: Basic Arithmetic
  Calculing additions

  Background: A Calculator
    Given a calculator I just turned on

  @issues:ISSUE-456,ISSUE-001
  Scenario: Addition
  # Try to change one of the values below to provoke a failure
    When I add 4 and 5
    Then the result is 9

  Scenario: Another Addition
  # Try to change one of the values below to provoke a failure
    When I add 4 and 7
    Then the result is 11
