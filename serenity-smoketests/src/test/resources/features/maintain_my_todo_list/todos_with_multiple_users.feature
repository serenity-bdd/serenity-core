Feature: Todos with multiple users
  More than one user should be able to use the Todo system simultaneously

  Scenario: Several users add new todos
    Given Jane has a todo list containing Buy some milk, Buy Petrol
    And Joe has a todo list containing Buy some bread, Go to the gym
    When Jane deletes the todo action Buy some milk
    Then Jane's todo list should contain Buy Petrol
    And Joe's todo list should contain Buy some bread, Go to the gym

