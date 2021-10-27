Feature: Buying things - with tables and errors

  Scenario Outline: Buying lots of widgets
    Given I want to purchase <amount> gizmos
    And a gizmo costs $<cost>
    When I order the gizmos
    Then I should pay $<total>
  Examples:
  | amount | cost | total |
  | 0      | 10   | 0     |
  | -1     | 10   | 10    |
  | 2      | 10   | 50    |
  | 2      | 0    | 0     |

