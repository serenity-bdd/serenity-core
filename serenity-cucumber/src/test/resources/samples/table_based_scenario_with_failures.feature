Feature: Buying things - with tables and failures screenplay

  Scenario Outline: Buying lots of widgets
    An error is thrown when the cost is negative

    Given I want to purchase <amount> widgets
    And a widget costs $<cost>
    When I buy the widgets
    Then I should be billed $<total>
  Examples:
  | amount | cost | total |
  | 0      | 10   | 0     |
  | 1      | -1   | 10    |
  | 2      | 10   | 50    |
  | 2      | 0    | 0     |

