@cucumber
@filtering
Feature: Filtering todos

  In order to make me feel **a sense of accomplishment**
  As a forgetful person
  I want to be to _view all of things I have completed_

  Scenario: View only the completed items
    Given that Jane has a todo list containing Buy some milk, Walk the dog
    And she completes the task called "Walk the dog"
    When she filters her list to show only Completed tasks
    Then her todo list should contain Walk the dog

  Scenario Outline: Viewing the items by status
    Given that Jane has a todo list containing <tasks>
    And she completes the task called "Walk the dog"
    When she filters her list to show only <filter> tasks
    Then her todo list should contain <expected>
    Examples:
      | tasks                       | filter    | expected                     |
      | Buy some milk, Walk the dog | Active    | Buy some milk                |
      | Buy some milk, Walk the dog | Completed | Walk the dog                 |
      | Buy some milk, Walk the dog | All       | Buy some milk,  Walk the dog |
