Feature: Game Rules

  The rules of Wordle are simple. Your objective is to guess a secret five-letter word in as few guesses as possible.
  To submit a guess, type any five-letter word and press enter.
  All of your guesses must be real words, according to a dictionary of five-letter words that Wordle allows as guesses.

  Players have to guess a five-letter word.
  - Correctly placed letters are shown as GREEN
  - Incorrectly placed letters are shown as YELLOW
  - Letters not appearing in the word are represented as GRAY

  Rule: Correctly placed letters appear in green
    Example: Correctly placed letters
      Given the target word is:
        | B | L | A | N | D |
      When the player enters the following letters:
        | P | R | I | N | T |
      Then the squares should be colored as follows:
        | GRAY | GRAY | GRAY | GREEN | GRAY |

  Rule: Incorrectly placed letters appear in yellow
    Example: Letters that are present but not in the right place
      Given the target word is:
        | B | L | A | N | D |
      When the player enters the following letters:
        | B | R | A | I | N |
      Then the squares should be colored as follows:
        | GREEN | GRAY | GREEN | GRAY | YELLOW |

    Scenario Outline: Letters that do not form valid words should be rejected
      Given the target word is:
        | B | L | A | N | D |
      When the player attempts to enter the following letters:
        | <Letter 1> | <Letter 2> | <Letter 3> | <Letter 4> | <Letter 5> |
      Then the attempt should be rejected
      Examples:
        | Letter 1 | Letter 2 | Letter 3 | Letter 4 | Letter 5 | Reason         |
        | T        | O        |          |          |          | Word too short |
        | A        | B        | C        | D        | E        | Not a word     |
        | F        | A        | S        | T        | !        | Not a word     |

  Rule: Repeated letters in the wrong spot appear in grey
    Example: Two incorrectly placed letters
      Given the target word is:
        | B | L | A | N | D |
      When the player enters the following letters:
        | L | A | B | E | L |
      Then the squares should be colored as follows:
        | YELLOW | YELLOW | YELLOW | GRAY | GRAY |

  Rule: The player wins when they find the right word in 6 or less tries
    Example: Player guesses the right word
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

    Example: Player runs out of tries
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
