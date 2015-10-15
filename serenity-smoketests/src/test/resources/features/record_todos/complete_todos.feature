Feature: Complete todos
  Once I have completed an item, I need to be able to check it off in my todo list.

  Scenario: Complete a todo action
    Given I need to buy some milk
    And I have added the todo action 'Buy some milk'
    When I mark the 'Buy some milk' action as complete
    Then 'Buy some milk' should appear as completed