@cucumber
Feature: See lateral menu

  As a curious person
  I want to _open the lateral menu_
  In order to **see what options I have**

  Scenario: Open the lateral menu
    Given that Andrei has a note list containing only the default notes
    When he opens the lateral menu
    Then he should see the Android icon
    And he should see "Notes" as the title
    And also he should see "Statistics" option

  Scenario: Hide the lateral menu swiping
    Given that Andrei is seeing the lateral menu
    When he swipes by his right to his left
    Then the lateral menu should be hidden
