Feature: Using Cucumber with Thucydides

  Scenario: Instantiating Thucydides step fields
    Given I have a Cucumber feature file containing Thucydides @Steps fields
    When I run it using Thucydides
    Then the step fields should be instantiated

  Scenario: Instantiating web-enabled Thucydides step fields
    Given I have a Cucumber feature file containing a web-enabled Thucydides @Steps fields
    When I run it using Thucydides
    Then the web-enabled step fields should be instantiated

  Scenario: Instantiating Thucydides Page Objects
    Given I have a Cucumber feature file containing Thucydides @Steps fields
    When I run it using Thucydides
    Then the nested pages objects should be instantiated

