Feature: Ignored scenarios

  @group:alpha
  @ignore
  Scenario: An ignored scenario
    Given some precondition
    When something should be ignored
    Then we should be happy

  @group:beta
  @ignore
  Scenario: A second ignored scenario
    Given some precondition
    When something should be ignored
    Then we should be happy

  @group:gamma
  @ignore
  Scenario: A third ignored scenario
    Given some precondition
    When something should be ignored
    Then we should be happy

  @group:alpha
  @ignore
  Scenario: A fourth ignored scenario
    Given some precondition
    When something should be ignored
    Then we should be happy
