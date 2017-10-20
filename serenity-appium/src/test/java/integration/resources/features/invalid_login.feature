Feature: Login page scenarios


Scenario: User is not allowed to login with invalid credentials
Given User is on login page
When Enter invalid credentials
Then  User is shown error message