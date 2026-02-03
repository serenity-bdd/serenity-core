@advanced @api
Feature: API Testing with Cucumber
  Demonstrates REST API testing using Cucumber with Serenity Screenplay.

  Background:
    Given the API is available

  @smoke @api
  Scenario: Fetch a user by ID
    When user 1 is requested
    Then the user name should be "Leanne Graham"
    And the response status should be 200

  @api @create
  Scenario: Create a new post
    When a post titled "My Cucumber Post" is created
    Then the post should be created successfully
    And the response status should be 201

  @api @multiple-users
  Scenario Outline: Fetch multiple users
    When user <user_id> is requested
    Then the user name should be "<expected_name>"

    Examples:
      | user_id | expected_name    |
      | 1       | Leanne Graham    |
      | 2       | Ervin Howell     |
      | 3       | Clementine Bauch |
