Feature: Using Screenplay Anonymous Performable

  @shouldPass
  Scenario: Should run steps after an anonymous performable passed
    Given I use the Screenplay Anonymous Performable
    When I make this anonymous performable task pass
    Then I see this step being executed

  @shouldFail
  Scenario: Should not run steps after an anonymous performable failed
    Given I use the Screenplay Anonymous Performable
    When I make this anonymous performable task fail
    Then I see this step is not executed

  @shouldFail
  Scenario Outline: Should run example after an anonymous performable failed
    Given I use the Screenplay Anonymous Performable
    When I make this anonymous performable task <outcome>
    Then I see this step is <run or not run> depending on the outcome of the previous step

    Examples:
      | outcome | run or not run |
      | fail    | not executed   |
      | pass    | executed       |

#  # cross check with dedicated task (instrumented)
#  @shouldFail
#  Scenario: Should not run steps after a dedicated instrumented task failed
#    When I make this dedicated instrumented task fail
#    Then I see this step is not executed

#  # cross check with dedicated task (non-instrumented)
#  @shouldFail
#  Scenario: Should not run steps after a dedicated non-instrumented task failed
#    When I make this dedicated non-instrumented task fail
#    Then I see this step is not executed
