@cucumber
@smoketest
Feature: Filtering things I need to do

  In order to make me feel a sense of accomplishment
  As a forgetful person
  I want to be to view all of things I have completed

  In a typical use case, Jane can manage her todos:
  {Scenario} View only completed items

  She can filter items when they are completed
  {Examples} Do many things


  Background: Set up stuff
    Given some stuff

  Rule: Only completed items must be seen when the list is accordingly filtered
    Here follows the rule description...

  Scenario: View only completed items
    Given that Jane has a todo list containing Buy some milk, Walk the dog
    And she has completed the task called 'Walk the dog'
    When she filters her list to show only Completed tasks
    Then her todo list should contain Walk the dog

  @Manual
  Scenario Outline: Do many things

    Given that Jane has a todo list containing <tasks>
    And she has completed the task called 'Walk the dog'
    When she filters her list to show only <filter> tasks
    Then her todo list should contain <expected>

    Examples: Do some things
      | tasks                       | filter    | expected      |
      | Buy some milk, Walk the dog | Completed | Walk the dog  |
      | Buy some milk, Walk the dog | Active    | Buy some milk |

    Examples: Do some other things
      | tasks                       | filter    | expected      |
      | Buy some milk, Walk the dog | Completed | Walk the dog  |
      | Buy some milk, Walk the dog | Active    | Buy some milk |


    Rule: Only completed items must be seen when the list is accordingly filtered with embedded tables

    Scenario: View only completed items with embeded tables
    Given that Jane has a todo list containing
    | Tasks         |
    | Buy some milk |
    | Walk the dog  |
    And she has completed the task called 'Walk the dog'
    When she filters her list to show only Completed tasks
    Then her todo list should contain Walk the dog

