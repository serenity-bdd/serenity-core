Feature: A simple feature that fails

  @shouldFail
  Scenario Outline: A simple failing scenario outline
    Given I want to purchase <count> widgets
    And at a cost of <cost>
    When I buy the widgets
    Then I should be billed $10
    Examples:
      | count | cost | total
      | 1     | 5    | 20
      | 2     | 5    | 10
