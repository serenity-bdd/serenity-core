Feature: Arithmetic

  @smoke
  Scenario: Adding passes
    Given I have 2 and 2
    Then the sum should be 4

  Scenario Outline: Adding several pairs
    Given I have <a> and <b>
    Then the sum should be <sum>

    Examples:
      | a | b | sum |
      | 1 | 1 | 2   |
      | 2 | 3 | 5   |
