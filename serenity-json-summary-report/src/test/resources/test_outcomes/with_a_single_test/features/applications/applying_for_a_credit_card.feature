Feature: Applying for a credit card
  In order to purchase items I want sooner
  As a bank customer
  I would like to apply for a credit card to suit my personal needs

  Different types of credit cards are available, including Personal Credit Cards, Business Credit Cards, and Executive Credit Cards

  Scenario: Applying for a business credit card
    Given Bill owns a business with an annual profit of $200000
    And a credit score category of Excellent
    When Bill applies for a Credit Card
    Then he should be proposed a Gold Credit Card package with a limit of $5000
