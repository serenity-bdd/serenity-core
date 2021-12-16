@foo
Feature: Basic Arithmetic with tables
  In order to do my maths homework
  As a maths student
  I want to be able to add sums

  Scenario Outline: Many additions
    Given a calculator I just turned on
    And I enter <a> and <b>
    And I press <op>
    Then the result is <c>

    Examples:
      | a  | b  | op | c  |
      | 10 | 20 | +  | 30 |
      | 20 | 5  | -  | 15 |
      | 25 | 0  | /  | 0  |
      | 6  | 9  | *  | 42 |
      | 2  | 2  | +  | 4  |
