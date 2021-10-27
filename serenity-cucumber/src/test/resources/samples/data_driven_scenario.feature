Feature: Buying things - data driven

  Scenario: Buying lots of widgets
    Given I want to purchase the following gizmos:
    | item | quantity | price |
    | A1   | 10       | 10    |
    | B2   | 5        | 40    |
    | C3   | 60       | 5     |
    When I buy the gizmos
    Then I should be billed the following for each item:
    | item | total |
    | A1   | 100   |
    | B2   | 200   |
    | C3   | 300   |


