Earning Frequent Flyer points from flights

Meta:
@earning-points
@tag component:backend
@issue FH-1,FH-12

Narrative:
In order to encourage travellers to book with Flying High Airlines more frequently
As the Flying High sales manager
I want travellers to earn Frequent Flyer points when they fly with us

Scenario: Earning standard points from an Economy flight
Given the flying distance between Sydney and Melbourne is 878 km
And I am a Bronze Frequent Flyer member
When I fly from Sydney to Melbourne on 10/10/2014
Then I should earn 439 points
