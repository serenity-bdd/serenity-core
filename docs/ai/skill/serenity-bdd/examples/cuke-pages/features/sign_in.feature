Feature: Signing in to the bookshop

  Scenario: A reader with the right password reaches the catalogue
    Given Reader is on the sign-in page
    When Reader signs in as "reader@example.com" with the password "correct-horse"
    Then the Catalogue page is shown

  Scenario: A reader with the wrong password is refused
    Given Reader is on the sign-in page
    When Reader signs in as "reader@example.com" with the password "wrong"
    Then Reader is told "Invalid email or password"

  Scenario Outline: The catalogue says how many books it holds
    Given Reader is on the sign-in page
    When Reader signs in as "reader@example.com" with the password "correct-horse"
    Then the catalogue lists <count> books

    Examples:
      | count |
      | 5     |
