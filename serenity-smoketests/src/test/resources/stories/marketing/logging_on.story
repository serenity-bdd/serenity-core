Logging on to the 'Flying High' admin site

Meta:
@tag component:ui
@issue FH-22

Scenario: Logging on successfully
Given Joe is a Marketing Admin
When Joe logs on with password 'secret'
Then he should be given access to the admin site
