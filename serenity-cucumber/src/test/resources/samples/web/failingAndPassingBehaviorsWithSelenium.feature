@driver:firefox
@driver-options:--headless
Feature: Failing and Passing Behaviors With Selenium

  Scenario: A failing scenario that uses selenium
    Given I am on the test page
    When I enter a first name Joe
    And I enter a last name Smith
    Then I should see first name Joe on the screen
    Then I should see last name Black on the screen

  Scenario: A passing scenario that uses selenium
    Given I am on the test page
    When I enter a first name Joe
    And I enter a last name Smith
    Then I should see first name Joe on the screen
    Then I should see last name Smith on the screen
