Feature: Buying things - with tables failures and errors

  Scenario Outline: Buying lots of widgets
    Given I want to purchase <amount> gizmos
    And a gizmo costs $<cost>
    When I order the gizmos
    Then I should pay $<total>
    Examples:
      | amount | cost | total |
      | 0      | 10   | 0     |
      | 2      | 10   | 20    |
      | -1     | 10   | 10    |
      | 3      | 10   | 30    |

