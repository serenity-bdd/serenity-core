@cucumber
@version:Release-2
Feature: Adding new items to the todo list

  In order to avoid having to remember things that need doing
  As a forgetful person
  I want to be able to record what I need to do in a place where I won't forget about them

  Scenario: Adding an item to an empty list
    Given that James has an empty todo list
    When he adds 'Buy some milk' to his list
    Then 'Buy some milk' should be recorded in his list

  Scenario: Adding an item to a list with other items
    Given that Jane has a todo list containing Buy some milk, Walk the dog
    When she adds 'Buy some cereal' to her list
    Then her todo list should contain Buy some milk, Walk the dog, Buy some cereal

  Scenario: Adding items to several peoples lists
    Given that James has a todo list containing Buy some milk, Walk the dog
    And that Jill has a todo list containing Buy some milk, Buy some cheese
    When she adds 'Buy some cereal' to her list
    Then Jill's todo list should contain Buy some milk, Buy some cheese, Buy some cereal
    And James's todo list should contain Buy some milk, Walk the dog
