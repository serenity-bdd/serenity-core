User Authentication

Narrative:
In order to prevent unauthorized use of member points
As the system admin
I want users to authenticate before they can access their account

Meta:
@tag component:ui
@issue FH-7
@wip

Scenario: Successful authentication
Given Jane is a registered Frequent Flyer
When Jane authenticates with a valid email address and password
Then Jane should be given access to her account

