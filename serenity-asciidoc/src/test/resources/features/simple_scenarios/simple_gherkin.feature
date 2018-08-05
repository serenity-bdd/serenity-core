Feature: Simple Gherkin

  In Cucumber, features are described in Feature Files, which contain a number of scenarios.
  Each feature is reported in it's own section, with the scenarios appearing as sub-sections.

  Scenario: Rendering simple Gherkin scenarios
    Given Tom is a customr of BDD Bank
    And Tom has a good credit rating
    When Tom applies for a credit card
    Then Tom should be proposed a standard credit card package
