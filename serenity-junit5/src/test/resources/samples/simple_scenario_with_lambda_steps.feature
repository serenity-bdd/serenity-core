Feature: A simple feature with lambda steps
  @shouldPass
  Scenario: A simple scenario
    Given I want to use a lambda step
    When I run the test
    Then I should see the output

