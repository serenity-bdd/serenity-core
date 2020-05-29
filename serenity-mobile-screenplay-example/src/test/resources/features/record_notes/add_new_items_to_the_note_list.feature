@cucumber
Feature: Add new todos

  In order to avoid having to remember things that need doing
  As a forgetful person
  I want to be able to record what I need to do in a place where I won't forget about them

  Scenario: Adding an item to an empty list in Cucumber
    When "Jacob" notes "Buy some milk" to his list
