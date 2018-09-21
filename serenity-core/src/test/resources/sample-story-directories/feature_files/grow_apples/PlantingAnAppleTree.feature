@issue:123

Feature: Planting a new apple tree
  As a farmer
  I want to plant an apple tree
  So that I can grow apples

  Apples are cool. We like apples. You can plant them too:
  {Scenario} Planting a green apple tree

  Scenario: Planting a green apple tree
    Given I need some apples
    When I plant an apple seed
    And I water it
    Then a tree should grow

  Scenario Outline: Planting another apple tree
    Given I need some <color> apples
    When I plant an apple seed
    And I water it
    Then a tree should grow
    Examples:
      | color |
      | red   |
      | green |