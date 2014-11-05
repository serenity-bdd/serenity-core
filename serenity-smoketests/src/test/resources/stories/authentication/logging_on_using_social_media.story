Logging on to the 'My Flying High' web site

Meta:
@iteration-1
@tag component:ui
@issue FH-7

Scenario: Logging on via Facebook
Meta:
@skip

Given Joe is a Frequent Flyer member
And Joe has registered online via Facebook
When Joe logs on with a Facebook token
Then he should be given access to the site

Scenario: Logging on via GMail

Given Joe is a Frequent Flyer member
And Joe has registered online via GMail
When Joe logs on with a GMail token
Then he should be given access to the site
