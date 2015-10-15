Feature: Add new todos
  I need to be able to jot down actions I need to do as fast as I think of them

  Scenario: Record a new todo action for future use
    Given I need to buy some milk
    When I add the todo action 'Buy some milk'
    Then 'Buy some milk' should be recorded in my todo list

  Scenario: New todos should be marked as Active
    Given I need to buy some milk
    When I add the todo action 'Buy some milk'
    Then 'Buy some milk' should be recorded in the Active items
