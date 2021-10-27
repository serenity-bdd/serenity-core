Feature: Using Serenity step libraries in Cucumber step definitions

  Scenario: Serenity instantiates step library fields in the Cucumber step definitions
    Given I want to use a step library
    When I add a step library field annotated with @Steps
    Then Serenity should instantiate the field

  Scenario: Serenity instantiates different step libraries for each field by default
    Given I want to use several step library fields of the same type
    When I add a step library fields to each of them
    Then Serenity should instantiate a different library for each field

  Scenario: Serenity preserves the state of a step library instance during a scenario
    Given I have a Serenity step library
    When I do something with the library
    Then the state of the library should be updated

  Scenario: Serenity refreshes the step libraries for each new scenario
    Given I have a Serenity step library
    When I start a new scenario
    Then the step library should be reinitialised

  Scenario: Shared step libraries refer to the same instance
    Given I have two Serenity step libraries
    When they are annotated with @Steps(shared=true)
    Then both should refer to the same instance

  Scenario: Storig information in the Serenity session state
    Given I have a Serenity step library
    When I store information the session state
    Then the session state information should be available in subsequent steps

  Scenario: Serenity session state should be reset for each scenario
    Given I have a Serenity step library
    When I start a new scenario
    Then the session state information from previous scenarios should be cleared
