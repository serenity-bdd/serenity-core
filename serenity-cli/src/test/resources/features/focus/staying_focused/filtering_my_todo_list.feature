@cucumber
Feature: Filtering my todo list

  In order to make me feel **a sense of accomplishment**
  As a forgetful person
  I want to be to view all of things I have completed

  Scenario: View only **completed** items #123
    Given that Jane has a todo list containing Buy some milk, Walk the dog
    And she has completed the task called 'Walk the dog'
    When she filters her list to show only Completed tasks
    Then her todo list should contain Walk the dog

  Scenario Outline: Viewing items by status
    Given that Jane has a todo list containing <tasks>
    And she has completed the task called 'Buy some milk'
    When she filters her list to show only <filter> tasks
    Then her todo list should contain <expected>
    Examples:
      | tasks                            | filter    | expected       |
      | Buy some milk, Walk the dog      | Active    | Walk the dog   |
      | Buy some milk, Walk the dog      | Completed | Buy some milk  |
      | Buy some milk, Walk the cat      | Active    | Walk the cat   |
      | Buy some milk, Feed the fish     | Completed | Buy some milk  |
      | Buy some milk, Walk the goat     | Active    | Walk the goat  |
      | Buy some milk, Feed the penguins | Completed | Buy some milk   |
      | Buy some milk, Walk the snake    | Active    | Walk the snake |
      | Buy some milk, Feed the pigeons  | Completed | Buy some milk   |
