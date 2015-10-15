Feature: Filter todos by status
  I need to be able show only completed or uncompleted tasks

  Scenario: Display only Active tasks
    Given Joe has a todo list containing Buy some milk,Buy Petrol
    And Joe has marked the Buy some milk action as complete
    When Joe consults the Active tasks
    Then Joe's todo list should contain Buy Petrol

  Scenario: Display Completed tasks
    Given Joe has a todo list containing Buy some milk,Buy Petrol
    And Joe has marked the Buy some milk action as complete
    When Joe consults the Completed tasks
    Then Joe's todo list should contain Buy some milk

  Scenario: Display All tasks
    Given Joe has a todo list containing Buy some milk,Buy Petrol
    And Joe has marked the Buy some milk action as complete
    When Joe consults All tasks
    Then Joe's todo list should contain Buy some milk,Buy Petrol