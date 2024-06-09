@singlebrowser
@authentication
Feature: Login is missing a scenario name

  Rule: Customers needs to provide valid credentials to access the site

    @test
    @smoke
    Example:
      Given Colin is on the login page
      When Colin logs in with valid credentials
      Then he should be presented the product catalog

    @test
    Scenario Outline: Login's with invalid credentials for <username>
      Given Colin is on the login page
      When Colin attempts to login with the following credentials:
        | username   | password   |
        | <username> | <password> |
      Then he should be presented with the error message <message>
      Examples:
        | username        | password       | message                                                     |
        | standard_user   | wrong_password | Username and password do not match any user in this service |
        | unknown_user    | secret_sauce   | Username and password do not match any user in this service |
        | unknown_user    | wrong_password | Username and password do not match any user in this service |
        | locked_out_user | secret_sauce   | Sorry, this user has been locked out                        |

