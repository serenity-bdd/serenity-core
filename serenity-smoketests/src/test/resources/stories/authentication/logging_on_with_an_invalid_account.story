Logging on to the 'My Flying High' web site

Meta:
@iteration-1
@tag component:ui
@issue FH-7
@expectedfailure

Narrative:
Given Joe is a Frequent Flyer member
And Joe has registered online with a password of 'secret'

Scenario: Logging on with an expired account
GivenStories: given/joe_has_registered.story
Given the account has expired
When Joe logs on with password 'wrong'
Then he should be informed that his account has expired
And he should be invited to renew his account

Scenario: Logging on with an incorrect password
GivenStories: given/joe_has_registered.story
When Joe logs on with password 'wrong'
Then he should be informed that his password was incorrect