Feature: Failed scenarios

  @group:alpha
  Scenario: A failing scenario
    Given some precondition
    When something is wrong
    Then we should not be happy

  @group:beta
  Scenario: A second failing scenario
    Given some precondition
    When something is wrong
    Then we should not be happy

  @group:gamma
  Scenario: A third failing scenario
    Given some precondition
    When something is wrong
    Then we should not be happy

