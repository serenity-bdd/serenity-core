Feature: Test iFixit application on ios device

Scenario: Verify items on Acura Support Community
Given User goes to Home page
And User clicks on Car and Truck category
And User clicks on Acura category
And User waits for Navigation Bar
Then Verify five items display: Acura Integra, Acura MDX, Acura RL, Acura TL, Acura TSX
