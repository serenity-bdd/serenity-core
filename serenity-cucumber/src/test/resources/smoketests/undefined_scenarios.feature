Feature: Using Undefined Scenarios

  Scenario: With steps that haven't been implemented yet
    Given I am Deep Thought
    When I discover the answer
    Then the answer should be 42

  Scenario: Another scenario with no defined steps
    Given I am Deep Thought
    When I discover the question
    Then the question should be what is 6 time 9
