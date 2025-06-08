@feature_equ=add_item
@feature_colon:add_item
@feature_add_item
Feature: Add new item to shopping list

  @scenario_equ=milk
  @scenario_colon:milk
  @scenario_add_item
  @scenario_milk
  Scenario: Add buying milk to the list
    Given Rama is looking at his shopping list
    When he adds "Buy some milk" to the list
    Then he sees "Buy some milk" as an item in the shopping list

  @scenario_equ=eggs
  @scenario_colon:eggs
  @scenario_add_item
  @scenario_eggs
  Scenario: Add buying eggs to the list
    Given Rama is looking at his shopping list
    When he adds "Buy some eggs" to the list
    Then he sees "Buy some eggs" as an item in the shopping list