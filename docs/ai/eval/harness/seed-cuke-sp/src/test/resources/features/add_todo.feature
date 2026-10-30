Feature: Todo list

  Scenario: Adding a todo
    Given Toby has opened the todo app
    When Toby adds "Buy milk" to the list
    Then Toby should see "Buy milk" in the list
