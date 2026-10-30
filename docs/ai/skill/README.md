# The `serenity-bdd` skill

A skill for Claude Code (and other agents that read `SKILL.md` skills) that helps write Serenity BDD tests in Java. It is in `serenity-bdd/`.

It covers every supported style and asks the user which one to use when that isn't clear. It does not prefer one:

| Approach | Drivers |
|---|---|
| Page Objects + step library (`@Steps`) | Selenium, Playwright |
| Screenplay | Selenium, Playwright, REST |
| Cucumber (with Screenplay) | Selenium |

All with JUnit 5.

## What is in it

```
serenity-bdd/
  SKILL.md                     how the agent picks the style, writes, compiles and fixes
  references/
    screenplay.md              exact packages, common mistakes, rules for Screenplay and Cucumber
    page-objects.md            the same for Page Objects (Selenium and Playwright)
    project-setup.md           dependencies per style, report plugin, serenity.conf
  examples/                    compiling reference code for each style
  check-examples.sh            compiles every example set against a Serenity release
```

`SKILL.md` stays short. The agent reads only the reference for the style in use.

## Installing it

Copy the `serenity-bdd` directory to one of these places:

- **One project:** `<project>/.claude/skills/serenity-bdd/`. This is what the evaluation used.
- **All your projects:** `~/.claude/skills/serenity-bdd/` (standard Claude Code location; not separately tested here).

Then ask for Serenity work as usual ("add a Serenity test for the login page"). The skill loads when the request matches its description. `check-examples.sh` isn't needed in an installed copy.

## What it was measured to do

Details are in `../baseline-results.md`. The evaluation scores whether generated code compiles; it does not run it. Only the reference examples are run (`check-examples.sh --run`). All models were Claude 5.5 models.

| Check | Without guidance | With the references or skill |
|---|---|---|
| One-shot compile rate, Screenplay prompts (held-out) | 38/72 | 69/72 |
| One-shot compile rate, Page Object prompts (held-out) | 36/45 | 45/45 |
| Agentic `mvn` runs per task | 3.2 | 1.9 |
| Final agentic code compiles | 66/66 | 66/66 |
| Asks instead of choosing a style when it is unclear | not applicable | 12/12 |

## Keeping it correct

The references describe Serenity **5.4.1** (Playwright modules **5.3.11**, the latest published). Class names and packages come from the released jars. When a new version ships:

1. Update the versions in `../eval/harness/gen.py` and run it to regenerate the harness projects.
2. Run `serenity-bdd/check-examples.sh`. It compiles each example set against its harness. Any `FAIL` means the references or examples need updating. It needs bash 4+ (macOS ships 3.2; use Homebrew bash) and Maven. Run once online to download dependencies, then it works offline.
3. Re-run the evaluation (below) if the references changed a lot.

When adding a rule to a reference, base it on a compile error you've seen, and add or change an example if the rule needs one.

## Re-running the evaluation

All commands run from `docs/ai/eval/`. Results are written to `results/`. They need the `claude` CLI, Maven, and Python 3.

```bash
# One-shot, no tools, compile only. --guidance prepends a file to each prompt.
python3 -I run.py results/my-run claude-sonnet-5-5 P04,P05 --runs 3 \
    --guidance ../skill/serenity-bdd/references/screenplay.md

# Agentic, with a compile loop. --skill installs the skill in the temporary project.
python3 -I run_agentic.py results/my-agentic claude-sonnet-5-5 P04,P08,S03 --runs 2 \
    --skill ../skill/serenity-bdd

# Same, but let the model stop and ask (use for the unnamed-style prompts P08, P09)
python3 -I run_agentic.py results/my-ask claude-sonnet-5-5 P08,P09 --skill ../skill/serenity-bdd --ask-ok
```

- Prompts are in `prompts.json`. P01–P08 are the original set, H01–H13 are held-out prompts on other apps, and S01–S04 add a test to a project that already has one style.
- Harness projects are in `harness/` (regenerate with `harness/gen.py`). `h-all` has every style's dependencies, and the `seed-*` projects contain one style's example code.
- Runs call the live `claude` CLI and cost money. The 132-run skill comparison cost about $15.
- The evaluation scores whether code compiles. It does not run browsers, so it can't show that a test passes.

## Known gaps

- Tests are not run. Runtime problems (timeouts, wrong locators on a real site) are not measured.
- The skill has not been tested with models other than Claude 5.5, or with agents other than Claude Code.
- Not covered: mobile (Appium), Gradle projects, JUnit 4 projects, Spock, serenity.conf options beyond the basics.
- The "ask" behaviour is measured up to the question. Following the answer in a second turn isn't.
