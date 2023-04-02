@singlebrowser @resetappstate @tax @accounting
Feature: Calculating sales tax

  Background:
    Given Colin has logged onto the application

  Rule: Sales tax is calculated as 8% of the purchase price
    Scenario Outline: Colin sees the correctly calculated sales tax for his order
      Given Colin has the following item in his cart:
        | <Item> |
      When he reviews his order
      Then the total price should be:
        | Item total | Tax   | Total   |
        | <Price>    | <Tax> | <Total> |
      Examples:
        | Item                  | Price  | Tax   | Total  |
        | Sauce Labs Backpack   | $29.99 | $2.40 | $32.39 |
        | Sauce Labs Bike Light | $9.99  | $0.80 | $10.79 |

  Rule: Sales tax is calculated on the total of all purchased items
    Scenario: Colin purchases two items so the total tax calculated includes tax for both items
      Given Colin has the following items in his cart:
        |Sauce Labs Backpack   |
        |Sauce Labs Bike Light |
      When he reviews his order
      Then the total price should be:
        | Item total | Tax   | Total  |
        | $39.98     | $3.20 | $43.18 |

