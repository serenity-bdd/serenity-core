@driver:htmlunit
@driver-options:--headless
Feature: Looking up a scenario suite

Scenario: Looking up the definition of 'apple'
GivenStories: stories/precondition/aPreconditionToLookUpADefinition.story
When the user looks up the definition of the word 'apple'
Then they should see the definition 'A common, round fruit produced by the tree Malus domestica, cultivated in temperate climates.'


Scenario: Looking up the definition of 'pear'
GivenStories: stories/precondition/aPreconditionToLookUpADefinition.story
When the user looks up the definition of the word 'pear'
Then they should see the definition 'A common, round fruit produced by the tree Malus domestica, cultivated in temperate climates.'