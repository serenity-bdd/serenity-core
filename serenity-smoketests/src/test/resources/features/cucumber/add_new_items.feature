Feature: Add new items

  Scenario: Add items to an empty list
    Given Toby starts with an empty list
    When he adds "Buy some milk" to his list
    Then the todo list should contain the following items:
      | Buy some milk |

  Scenario: Add two items to an empty list
    Given Toby starts with an empty list
    When he adds "Buy some milk" to his list
    And he adds "Buy some cheese" to his list
    Then the todo list should contain the following items:
      | Buy some milk |
      | Buy some cheese |


  Scenario Outline: Add different items to an empty list
    Given Toby starts with an empty list
    When he adds "<task>" to his list
    Then the todo list should contain the following items:
      | <task>|
    Examples:
    | task |
    | Buy some milk |
    | Buy some cheese |
    | Buy some cereal |
    | Buy some beef |

