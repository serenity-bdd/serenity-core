@cucumber
Feature: See note details

  As a forgetful person
  I want to _view all the details in any note_
  In order to **be sure that I'm not losing any information**

  Scenario: Andrei sees the first detail note
    Given that Andrei has a note list containing only the default notes
    When he sees the first detail note
    Then he should see the right title and description
    And these values shouldn't be modifiable

  Scenario: Andrei sees the 10th detail note
    Given that Andrei has a note list containing 10 notes
    When he sees the last detail note
    Then he should see the right title and description
    And these values shouldn't be modifiable