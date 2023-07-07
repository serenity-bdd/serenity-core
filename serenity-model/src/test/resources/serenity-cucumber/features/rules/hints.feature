Feature: Hints

  As a Wordle player
  I want to be able to receive hints when I'm stuck
  So that I can keep the game progressing and learn new words

  Rule: Hints should be proposed based on the structure and content of the word

    Scenario Outline: Possible hints for a word
      Given the target word is "<Word>"
      When the player requests a hint
      Then the proposed hints should include "<Hint Text>"
      Examples:
        | Word  | Hint Type          | Hint Text                         |
        | APPLE | Starts with letter | The word starts with the letter A |
        | CREEK | Ends with letter   | The word ends with the letter K   |
        | GROWL | Single vowel       | The word contains 1 vowel         |
        | APPLE | Multiple vowels    | The word contains 2 vowels        |
        | CRYPT | No vowels          | The word contains no vowels       |

  Rule: A random hint should be proposed for a given word
    Example: The word starts with a particular letter
      Given the target word is "APPLE"
      When the player requests a hint
      Then the proposed hint should be one of:
        | The word starts with the letter A |
        | The word ends with the letter E   |
        | The word contains 2 vowels        |

    Example: If the player asks for another hint, all the hints should be shown
      Given the target word is "QUEST"
      And the player has requested a hint
      When the player requests another hint
      Then the following hints should be proposed:
        | The word starts with the letter Q |
        | The word ends with the letter T   |
        | The word contains 2 vowels        |
