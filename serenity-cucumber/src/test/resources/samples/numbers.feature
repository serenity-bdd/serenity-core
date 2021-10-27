Feature: Add two numbers

  Scenario: This scenario should be marked as pending

    Given the number 1 and the number 4
    When 1 plus 4
    Then the result is equals to 5
    And a PendingException should be thrown
