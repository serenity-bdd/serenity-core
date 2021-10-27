@driver:phantomjs
@driver-options:--headless
@UniqueBrowser
Feature: A feature that uses two selenium scenarios

Scenario: A web scenario that uses selenium

Given I am on the test page
When I type in the first name Jack
Then I should see first name Jack on the screen

Scenario: A web scenario that uses selenium v2

Given I am on the test page
When I type in the first name Jill
Then I should see first name Jill on the screen