Earning extra points from Frequent Flyer status

Meta:
@issue FH-12
@tag component:backend

Narrative:
In order to encourage travellers to book with Flying High Airlines more frequently
As the Flying High sales manager
I want travellers to earn extra Frequent Flyer points when they fly with us regularly

Scenario: Earning points on flights by Frequent Flyer status
Given a member has a status of <initialStatus>
When I fly on a flight that is worth <base> base points
Then I should earn a status bonus of <bonus>
And I should have guaranteed minimum earned points per trip of <minimum>
And I should earn <total> points in all
Examples:
| initialStatus | base | bonus | minimum | total | notes               |
| Bronze        | 439  | 0     | 0       | 439   |                     |
| Silver        | 439  | 220   | 500     | 659   | minimum points      |
| Silver        | 148  | 111   | 500     | 500   | 50% bonus           |
| Gold          | 474  | 400   | 1000    | 1000  | minimum points      |
| Gold          | 2041 | 1531  | 1000    | 3572  | 75% bonus           |




