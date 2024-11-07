@pending
Feature: A feature with pending scenarios

  Scenario: Simple scenario 1
    Given I want to purchase 2 widgets
    And a widget costs $5
    When I buy the widgets
    Then I should be billed $10

  Scenario: Simple scenario 2
    Given I want to purchase 4 widgets
    And a widget costs $3
    When I buy the widgets
    Then I should be billed $12

  Scenario: Simple scenario 3
    Given I want to purchase 5 widgets
    And a widget costs $3
    When I buy the widgets
    Then I should be billed $15