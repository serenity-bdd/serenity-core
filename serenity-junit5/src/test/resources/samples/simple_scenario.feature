Feature: A simple feature
  @shouldPass
  Scenario: A simple scenario
    Given I want to purchase 2 widgets
    And a widget costs $5
    When I buy the widgets
    Then I should be billed $10

  Scenario: A simple scenario 2
    Given I want to purchase 2 widgets
    And a widget costs $5
    When I buy the widgets
    Then I should be billed $10


