Feature: A simple feature

  Rule: This is a simple rule
    Simple first rule description
    Scenario: A simple scenario
      Given I want to purchase 2 widgets
      And a widget costs $5
      When I buy the widget
      Then I should be billed $10

  Rule: This is a simple second rule
    Simple second rule description
    Scenario: A simple second scenario
      Given I want to purchase 2 widgets
      And a widget costs $5
      When I buy the widgets
      Then I should be billed $10

