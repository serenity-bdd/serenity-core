Feature: Buying things - with tables and errors

  Scenario Outline: Buying lots of widgets
    Given I want to purchase <amount> gizmos
    And a gizmo costs $<cost>
    When I order the gizmos
    Then I should pay $<total>
  Examples:
  | amount | cost | total |
  | 2      | 10   | 20    |
  | 2      | 10   | 30    |
  | 2      | 0    | 0     |
  | -1      | 0    | 0     |
  | 2      | 5   | 10    |


