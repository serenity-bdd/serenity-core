@cucumber
Feature: Add notes

  As a forgetful person
  I want to _view all of things that I have to remember_
  In order to **don't forget any important matter**

  Scenario: Andrei adds a note with valid and complete information
    Given that Andrei has a note list containing only the default notes
    When he creates a note with valid and complete information
    Then his note list should contains the new note

  Scenario: Andrei can't create a note without title
    Given that Andrei has a note list containing only the default notes
    When he creates a note without a title
    Then he should see this warning message: "Notes cannot be empty"

  Scenario: Andrei can't create a note with only blanks as title
    Given that Andrei has a note list containing only the default notes
    When he creates a note only with blanks as title
    Then he should see this warning message: "Notes cannot be empty"

  Scenario: Andrei adds a note with a picture
    Given that Andrei has a note list containing only the default notes
    When he creates a note with a picture
    Then his note list should contains the new note
    And the note detail should contains a picture

  Scenario: Andrei cancel the creation
    Given that Andrei has a note list containing only the default notes
    When he tries to create a note and cancels
    Then his note list should contains only the default notes

  Scenario: Andrei adds 2 notes
    Given that Andrei has a note list containing only the default notes
    When he creates 2 notes with valid and complete information
    Then his note list should contains all the new notes

  #Scenario: Andrei can insert 500 characters as description
  #  Given that Andrei has a note list containing only the default notes
  #  When he creates a note with 500 characters as description
  #  Then his note list should contains the new note only displaying the first characters in the description
  #  And the note details should contains the whole description

  #Scenario: Andrei can't insert 501 characters as description
  #  Given that Andrei has a note list containing only the default notes
  #  When he creates a note with 501 characters as description
  #  Then he should see this warning message: "Description is too long"
