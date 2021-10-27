@driver:phantomjs
@driver-options:--headless
Feature: Skipping Scenarios

  @pending
  Scenario: Pending scenario
    Given I want to search for something
    When I lookup apple
    Then I should see "apple at DuckDuckGo" in the page title

  @wip
  Scenario: WIP scenario
    Given I want to search for something
    When I lookup apple
    Then I should see "apple at DuckDuckGo" in the page title

  @skip
  Scenario: Skipping a scenario
    Given I want to search for something
    When I lookup apple
    Then I should see "apple at DuckDuckGo" in the page title

  @manual
  Scenario: A manual scenario
    Given I want to search for something
    When I lookup apple
    Then I should see "apple at DuckDuckGo" in the page title

  @ignore
  Scenario: An ignored scenario
    Given I want to search for something
    When I lookup apple
    Then I should see "apple at DuckDuckGo" in the page title

  @manual
  @skip
  Scenario: A skipped manual scenario
    Given I want to search for something
    When I lookup apple
    Then I should see "apple at DuckDuckGo" in the page title
