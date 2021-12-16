@skip
Feature: A feature with multiple scenarios

  Scenario: Simple scenario 1
    Given I want to purchase 2 widgets
    And a widget costs $5
    When I buy the widgets
    Then I should be billed $50

  Scenario: Simple scenario 2
    Given I want to purchase 4 widgets
    And a widget costs $3
    When I buy the widgets
    Then I should be billed $12