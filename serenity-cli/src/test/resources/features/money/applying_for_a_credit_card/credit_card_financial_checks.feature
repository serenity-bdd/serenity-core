Feature: Credit card financial checks
  In order to ensure that credit cards are not given to customers who will be unable to repay them
  As a financial institution
  I want to ensure that only clients with sufficient annual income are provided with a credit card

  Background:
    Given an individual customer with an account

  Scenario Outline: The maximum credit card limit depends on the customer's salary

  A customer needs a salary of at least £10,000. There are two types of card,
  one with a limit of £2500, and another with a limit of £5000

    Given an individual customer with an annual salary of <Salary>
    When the customer applies for a credit card
    Then the credit card application should be <Approved or Refused>
    And if approved, the maximum credit limit should be <Max Limit>

    Examples: Credit card approval for different salary levels
      | Salary   | Approved or Refused | Max Limit | Notes                       |
      | £5000    | Refused             | 0         | Salary must be over £10,000 |
      | £15,000  | Approved            | £2500     | Up to £15,000               |
      | £25,000  | Approved            | £5000     | Over £15,000                |
      | £100,000 | Approved            | £5000     | £5000 is the max limit      |