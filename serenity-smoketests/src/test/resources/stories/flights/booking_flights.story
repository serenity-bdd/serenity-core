Narrative:
In order to travel to my destination
As a traveller
I want to be able to book a flight online

Meta:
@tag component:ui
@issue FH-15

Scenario: View all available flights before booking
Given the following flight timetable:
| number | departure | destination | time  |
| FH-101 | Syndey    | Hong Kong   | 10:50 |
| FH-101 | Syndey    | London      | 23:50 |
| FH-102 | Syndey    | London      | 10:30 |
| FH-101 | Syndey    | Paris       | 13:50 |
When I search for return flights from Sydney to London in Business
Then I should see the following flights:
| number | departure | destination | time  |
| FH-101 | Syndey    | London      | 23:50 |
| FH-102 | Syndey    | London      | 10:30 |

Scenario: Filter flights by city before booking
Given I want to book a flight
When I enter 'Se' into the 'from' city field
Then I should see the following cities: Seattle, Seoul
