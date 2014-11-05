Meta:
@tag component:ui
@issue FH-18

Scenario: Display flight details by flight number
Given I need to know the details of flight number FH-101
When I request the details about this flight
Then I should receive the following:
| flightNumber | departure | destination  | time   |
| FH-101       | MEL       | SYD          | 06:00  |


Scenario: Display international flight details by flight number
Given I need to know the details of flight number FH-102
When I request the details about this flight
Then I should receive the following:
| flightNumber | departure | destination  | time   |
| FH-102       | SYD       | HKG          | 11:55  |