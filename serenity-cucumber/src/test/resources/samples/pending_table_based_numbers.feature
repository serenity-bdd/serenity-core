Feature: Add two numbers

  @pending
  Scenario Outline: This scenario should be marked as pending

    Given the number <a> and the number <b>
    When <a> plus <b>
    Then the result is equals to <c>


    Examples:
      | a | b | c |
      | 1 | 4 | 5 |
      | 2 | 7 | 9 |