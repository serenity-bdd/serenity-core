Feature: Wordle Overview

  The rules of Wordle are simple. Your objective is to guess a secret five-letter word in as few guesses as possible.
  To submit a guess, type any five-letter word and press enter.
  All of your guesses must be real words, according to a dictionary of five-letter words that Wordle allows as guesses.

  Players have to guess a five-letter word.
  - Correctly placed letters are shown as GREEN
  - Incorrectly placed letters are shown as YELLOW
  - Letters not appearing in the word are represented as GRAY

  Example: Player guesses the right word and wins
    Given the target word is:
      | B | L | A | N | D |
    When the player enters the following letters:
      | B | E | A | S | T |
      | B | R | A | I | N |
      | B | L | A | N | D |
    Then the squares should be colored as follows:
      | GREEN | GRAY  | GREEN | GRAY  | GRAY   |
      | GREEN | GRAY  | GREEN | GRAY  | YELLOW |
      | GREEN | GREEN | GREEN | GREEN | GREEN  |
    And the player should win the game

  Example: Player runs out of tries and loses
    Given the target word is:
      | B | L | A | N | D |
    When the player enters the following letters:
      | B | E | A | S | T |
      | B | R | A | I | N |
      | P | L | A | I | N |
      | P | L | A | N | E |
      | P | L | A | N | T |
      | P | L | A | N | K |
    Then the player should lose the game
