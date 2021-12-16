Feature: Buying things - with tables

  Background: I already have some cash
    Given I have $100

  @shouldPass
  Scenario Outline: Buying lots of widgets
    Given I want to purchase <amount> widgets
    And a widget costs $<cost>
    When I buy the widgets
    Then I should be billed $<total>
  Examples:
    | amount | cost | total |
    | 0      | 10   | 0     |
    | 1      | 10   | 10    |
    | 2      | 10   | 20    |
    | 3      | 10   | 30    |
    | 4      | 0    | 0     |

  Examples:
    | amount | cost | total |
    | 50     | 10   | 500   |
    | 60     | 10   | 600   |

  Scenario Outline: Buying more widgets
    Given I want to purchase <amount> widgets
    And a widget costs $<cost>
    When I buy the widgets
    Then I should be billed $<total>
  Examples:
    | amount | cost | total |
    | 6      | 10   | 0     |
    | 8      | 10   | 80    |
    | 10     | 10   | 100   |
