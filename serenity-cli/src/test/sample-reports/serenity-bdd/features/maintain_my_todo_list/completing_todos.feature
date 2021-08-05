@cucumber
@driver:chrome
Feature: Completing todos

  In order to make me feel **a sense of accomplishment**
  As a forgetful person
  I want to be to _view all of things I have completed_

  Scenario: Mark a task as completed
    Given that Jane has a todo list containing Buy some milk, Walk the dog
    When she completes the task called 'Walk the dog'
    And she filters her list to show only Completed tasks
    Then her todo list should contain Walk the dog

  Scenario: List of completed items should be empty if nothing has been completed
    Given that Jane has a todo list containing Buy some milk, Walk the dog
    When she filters her list to show only Completed tasks
    Then her todo list should be empty

