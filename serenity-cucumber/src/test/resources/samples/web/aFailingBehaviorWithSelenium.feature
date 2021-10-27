@driver:htmlunit
Feature: A failing scenario that uses selenium

Scenario Outline: A failing scenario that uses selenium
  Given I am on the test page
  When I type in the first name <firstname>
  And I type in the last name <lastname>
  Then I should see entered values of <expectedFirstname> and <expectedLastname>

Examples:
|firstname|lastname| expectedFirstname | expectedLastname |
|Joe      | Blanc  | Jack              | Black            |
|John     | Doe    | John              | Doe              |
