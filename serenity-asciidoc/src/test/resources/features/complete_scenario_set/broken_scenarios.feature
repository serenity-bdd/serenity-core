Feature: Broken scenarios

  @group:alpha
  Scenario: A broken scenario
    Given some precondition
    When something is broken
    Then we should not be happy

  @group:beta
  Scenario: A second broken scenario
    Given some precondition
    When something is broken
    Then we should not be happy

