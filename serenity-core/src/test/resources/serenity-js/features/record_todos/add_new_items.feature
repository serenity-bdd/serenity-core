Feature: Add new items to the todo list

  In order to avoid having to remember things that need doing
  As a forgetful person
  I want to be able to record what I need to do in a place where I won't forget about them

  Scenario: Adding an item to an empty list
    Given that James has an empty todo list
    When he adds 'Buy some milk' to his list
    Then 'Buy some milk' should be recorded in his list

  Scenario: Adding an item to a list with other items
    Given that James has a todo list containing Buy some cookies, Walk the dog
    When he adds 'Buy some cereal' to his list
    Then his todo list should contain Buy some cookies, Walk the dog, Buy some cereal

  Scenario Outline: Adding an item to a list with other items (<example>)
    Given that James has an empty todo list
    When he adds '<item>' to his list
    Then '<expected>' should be recorded in his list

    Examples:
      | example  | item          | expected      |
      | 1        | Buy some milk | Buy some milk |
      | 2        | Walk the dog  | Walk the dog  |

#  Scenario Outline: Do many things
#    Given that Jane has a todo list containing <tasks>
#    And she has completed the task called 'Walk the dog'
#    When she filters her list to show only <filter> tasks
#    Then her todo list should contain <expected>
#
#    Examples:
#      | tasks                       | filter    | expected      |
#      | Buy some milk, Walk the dog | Completed | Walk the dog  |
#      | Buy some milk, Walk the dog | Active    | Buy some milk |