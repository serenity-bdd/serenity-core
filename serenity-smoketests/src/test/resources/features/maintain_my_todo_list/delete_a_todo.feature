Feature: Delete a todo
  I need to be able to delete a todo item if I made a mistake or no longer need to do it.

  Scenario: Delete an active todo
    Given Jane has a todo list containing Buy some milk, Buy Petrol
    When Jane deletes the todo action Buy some milk
    Then Jane's todo list should contain Buy Petrol

  Scenario: Delete a completed todo
    Given Joe has a todo list containing Buy some milk,Buy Petrol
    And Joe has marked the Buy some milk action as complete
    When Joe deletes the todo action Buy some milk
    Then Joe's todo list should contain Buy Petrol
