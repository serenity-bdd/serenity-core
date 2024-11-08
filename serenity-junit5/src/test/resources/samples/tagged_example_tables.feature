Feature: Tagged Tables

  Scenario Outline: This scenario should have two tables

    Given the number <a> and the number <b>
    When <a> plus <b>
    Then the result is equals to <c>

    @small
    Examples: Small numbers
      | a | b | c |
      | 1 | 4 | 5 |
      | 2 | 7 | 9 |
      | 3 | 6 | 9 |

    @big
    Examples: Big numbers
      | a  | b | c  |
      | 11 | 4 | 15 |
      | 12 | 7 | 19 |