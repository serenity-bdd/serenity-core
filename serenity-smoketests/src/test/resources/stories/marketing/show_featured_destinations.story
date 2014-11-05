Showing featured destinations

Narrative:
In order to encourage travellers to travel more
As an airline
I want to show travellers exciting destinations when they log on

Meta:
@tag component:ui
@issue FH-21

Scenario: Displaying featured destinations
Given Jane has logged on
When Jane views the home page
Then she should see 3 featured destinations
And the featured destinations should include Singapore costing 900

Scenario: Viewing available flights
Given I want to book a flight
When I search for return flights from Sydney to London in Business
Then I should see the list of all available flights