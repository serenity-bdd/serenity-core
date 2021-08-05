@cucumber
Feature: Add ToDos

  In order to avoid having to remember things that need doing
  As a forgetful person
  I want to be able to record them as soon as I learn about them

  @protractor
  Scenario: Adding an item to an empty list

    Given James has an empty todo list
     When he adds 'Buy some milk' to his list
     Then 'Buy some milk' should be recorded in his list

  @protractor
  Scenario: Adding an item to a list with other items

    Given James has a todo list containing:
      | item          |
      | Buy some milk |
      | Walk the dog  |
     When he adds 'Buy some cereal' to her list
     Then his todo list should contain:
      | item            |
      | Buy some milk   |
      | Walk the dog    |
      | Buy some cereal |
