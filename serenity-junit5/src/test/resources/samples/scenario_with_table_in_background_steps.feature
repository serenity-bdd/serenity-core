Feature: Locate a customer by personal details and Reg Number
  In order to find information relevant to a specific customer
  As a financial advisor
  I want to be able to locate a customer by personal details, registration number

  Background:
    # Set up the customer in the test data, or ensure that this data is available
    Given the following customers exist:
      | Name         | DOB        | Mobile Phone | Home Phone | Work Phone | Address Line 1 | Address Line 2 |
      | SEAN PAUL    | 30/05/1978 | 860123334    | 1234567899 | 16422132   | ONE BBI ACC    | BEACON SOUTH   |
      | TONY SMITH   | 10/10/1975 | 86123335     | 11255555   | 16422132   | 1 MAIN STREET  | BANKCENTRE     |
      | PETE FORD    | 12/03/1970 | 865555551    | 15555551   | 15555551   | Q6B HILL ST    | BLACKROCK      |
      | JOHN B JOVI  | 22/08/1957 | 871274762    |            | 16422132   | BLAKBURN       | TALLAGHT       |
      | JOHN ANFIELD | 20/05/1970 | 876565656    | 015555551  | 214555555  | DUBLIN         | DUBLIN         |
    And I am logged into the OneView app

  @layer:ui
  Scenario: Locating a customer using a unique criterion
    When I locate a customer with a Reg Number of 80862061
    Then I should see the customer profile for TONY SMITH
