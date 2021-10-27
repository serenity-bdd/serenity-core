Feature: Illegal step definition libraries

  @shouldFail
  Scenario: A scenario using a step definition library without a default constructor
    Given I have a step library that fails to instantiate
    When I run it using Thucydides
    Then the tests should fail with an exception

