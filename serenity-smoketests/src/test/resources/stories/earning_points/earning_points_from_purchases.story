Earning Frequent Flyer points from purchases

Meta:
@earning-points
@tag component:backend
@issue FH-10

Narrative:
In order to encourage travellers to book with Flying High Airlines more frequently
As the Flying High sales manager
I want travellers to earn Frequent Flyer points when they make purchases with partner organizations

Scenario: Earning points from credit card purchases
Given pending
When pending
Then pending

Scenario: Earning points from purchases with partners
Given we can earn points with partners
And the flying distance between Sydney and Melbourne is 878 km
And I am a Bronze Frequent Flyer member
When I fly from Sydney to Melbourne on 10/10/2014
Then I should earn 439 points