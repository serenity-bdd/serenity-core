Feature: A feature file with a syntax error

  Scenario: This scenario has a syntax error
    Given that Jane has a todo list containing Buy some milk, Walk the dog
    Et she has completed the task called 'Walk the dog'
    When she filters her list to show only Completed tasks
    Then her todo list should contain Walk the dog

