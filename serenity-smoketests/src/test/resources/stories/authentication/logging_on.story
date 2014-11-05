Logging on to the 'My Flying High' web site

Meta:
@wip
@iteration-1
@tag component:ui
@issue FH-7

Narrative:
In order to use the system and access all of my personal data
I need to be able to log on

Given Joe is a Frequent Flyer member
And Joe has registered online with a password of 'secret'

Scenario: Logging on successfully
GivenStories: given/joe_has_registered.story
When Joe logs on with password 'secret'
Then he should be given access to the site


