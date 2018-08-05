Feature: Mixed scenarios

  @group:alpha
  Scenario: A mixed passing scenario
    Given some precondition
    When something good happens
    Then we should be happy

  @group:beta
  Scenario: A mixed pending scenario
    Given some precondition
    When something is not ready
    Then we should be happy

  @group:gamma
  Scenario: A mixed failing scenario
    Given some precondition
    When something is wrong
    Then we should be happy

  @group:alpha
  Scenario: A mixed broken scenario
    Given some precondition
    When something is broken
    Then we should be happy

  @group:beta
  @ignore
  Scenario: A mixed ignored scenario
    Given some precondition
    When something should be ignored
    Then we should be happy

  @group:gamma
  Scenario: A mixed compromised scenario
    Given some precondition
    When something is compromised
    Then we should be happy