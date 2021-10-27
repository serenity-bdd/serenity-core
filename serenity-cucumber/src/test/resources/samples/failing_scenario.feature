Feature: A simple feature that fails

  @shouldFail
  Scenario: A simple failing scenario
    Given I want to purchase 2 widgets
    And a widget costs $5
    When I buy the widgets
    Then I should be billed $20
    And I want to purchase 5 widgets

  Scenario: A simple passing scenario
    Given I want to purchase 2 widgets
    And a widget costs $5
    When I buy the widgets
    Then I should be billed $10
    And I want to purchase 5 widgets
