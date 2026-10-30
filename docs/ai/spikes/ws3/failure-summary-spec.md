# WS3 spike: a failure summary for agents

Status: spike findings and a proposed format. Not an implemented feature. Prototype: `failures.py` (reads a Serenity output directory, prints JSON or Markdown). Serenity 5.4.1.

## Question

After `mvn verify` fails, an agent has to scrape console output or HTML. Can a compact summary be derived from what Serenity already writes, and what is missing?

## What the spike looked at

| Output directory | Source | Outcomes |
|---|---|---|
| Deliberate failures (JUnit 5, nested `@Steps`, Screenplay REST) | built for the spike | 5: 1 pass, 2 assertion failures, 1 exception, 1 ignored |
| Deliberate failure (Cucumber with Screenplay project, plain steps) | built for the spike | 2: 1 pass, 1 failure |
| `serenity-screenplay-playwright` | existing `target/site/serenity` | 245: 240 pass, 5 error |
| `serenity-smoketests`, `serenity-tutorials`, `serenity-playwright-demo` | existing | 42, 40, 17: all pass |

Not covered: a failing web test with screenshots (the Playwright directory's errors are not UI failures), JUnit 4, parameterised tests, retries, JIRA/requirements providers, parallel runs.

## What Serenity already provides

One `<hash>.json` outcome file per test in the output directory (the same files used for the HTML report). Per outcome:

- `result` (`SUCCESS`, `FAILURE`, `ERROR`, `COMPROMISED`, `IGNORED`, `PENDING`...), `title`, `duration`, `scenarioId`, `testSource` (`JUnit5`, `Cucumber`).
- `testFailureClassname`, `testFailureMessage`, `testFailureCause` (`errorType`, `message`, `stackTrace`) and a one-line `testFailureSummary`.
- `testSteps`: a tree (`children`), each step with `description`, `result`, `screenshots` (`{screenshot: "<file>.png"}`, relative to the output directory) and, for the failing one, an `exception` object with the same shape as `testFailureCause`.
- `userStory.path` (requirement path), `testCaseName` and `methodName` for JUnit.

So the failing step path, the error, the user-code location and the last screenshot can be derived without any new data collection. The prototype does this in about 130 lines; it runs in 20 ms on 245 outcomes and produces 3.7 KB of JSON for 5 failures.

## Findings that shape the design

1. **A failed `mvn verify` leaves no HTML report.** The outcome JSON is written at test time, but the aggregate report runs in `post-integration-test`, which is never reached when tests fail. In the spike the output directory had JSON and JUnit XML but no `index.html`. So any summary must read the JSON outcomes (or be written at test time), not the HTML. This answers roadmap open question 3 in favour of a summary written when the tests finish, by a listener, not by report aggregation.
2. **Stack traces are already trimmed to one user frame** (`FailuresTest.java:44`), which is what an agent needs. Some errors have none (all 5 Playwright errors; Cucumber).
3. **Not every failure belongs to a step.** In all 5 Playwright errors the exception is on the test, after the steps ran (one outcome's only step is a successful "Background"), so there is no failing step to point at. The summary needs `failingStep: null` as a normal case.
4. **Cucumber outcomes have less location data.** No test class or method; `scenarioId` is `classpath:features/maths:Adding fails`; the requirement path is the feature file name (`maths`); there is no line number in the feature file; and no stack frame. An agent needs the feature file path and scenario line to edit the right place, so these would have to be added.
5. **Parent steps can lack a `result`** (the outer step of a nested pair has none in the JSON); the failing step is the deepest child with an `exception`.
6. **One outcome has no `userStory`, `testSteps` or `testCaseName`** (a failure before any step ran). Every field except `result` and `scenarioId` must be treated as optional.
7. **Assertion messages can repeat.** The Cucumber failure reports `MultipleAssertionErrors` with the same assertion twice ("The following 2 assertions failed: 1) expected: 5 but was: 4 2) expected: 5 but was: 4").
8. **Screenshots exist only for UI steps** and are relative to the output directory; the HTML report file name is a hash, so a per-test HTML link is only available after aggregation.

## Proposed format (`failures.json`, schema 0.1)

```json
{
  "schemaVersion": "0.1",
  "outputDir": "target/site/serenity",
  "summary": {"total": 5, "success": 1, "failure": 2, "error": 1, "ignored": 1},
  "failures": [
    {
      "scenarioId": "...",
      "title": "Fails an assertion inside nested steps",
      "result": "FAILURE",
      "requirement": "demo.FailuresTest",
      "test": {"class": "demo.FailuresTest", "method": "nestedAssertionFailure", "source": "JUnit5"},
      "location": {"file": "FailuresTest.java", "line": 41, "class": "demo.FailuresTest", "method": "nestedAssertionFailure"},
      "failingStep": {"path": ["Outer step for the basket", "Check that 3 equals 4"], "number": 2},
      "error": {"type": "org.opentest4j.AssertionFailedError", "message": "expected: 4 but was: 3"},
      "screenshot": null,
      "outcomeFile": "<hash>.json",
      "htmlReport": null,
      "durationMs": 8
    }
  ]
}
```

Rules: failures sorted by requirement then title (stable, diffable); message capped (default 500 characters, with an ellipsis); `failingStep`, `location` and `screenshot` may be null; `htmlReport` only if the file exists; the schema is versioned and changes are breaking.

Proposed additions that the current data cannot support: a Cucumber `feature` file path and scenario `line`, a test source file path (not only `FailuresTest.java`), and de-duplicated assertion messages.

Markdown view (same data, for people and for agents that prefer prose): see `failures.py --format markdown`.

## CLI shape

```
serenity failures [--format json|markdown] [--max-message N] [--output-dir DIR]
serenity summary  [--format json|markdown]
serenity outcome  <scenarioId|title> [--format json|markdown]   # full steps for one test
```

The module already exists (`serenity-cli`); the prototype's logic would move there, with the file written at test end as well.

## Open questions

1. **Writer.** A listener that writes `failures.json` at the end of the run (recommended by finding 1), or only the CLI? Both can share one implementation.
2. **Cucumber location data.** Where can the feature file path and line come from (the Cucumber pickle)? This needs a change in `serenity-cucumber`.
3. **Parallel and retried runs.** Several outcomes for one scenario may exist; which one wins?
4. **Message shaping.** How should multi-assertion and soft-assertion failures be summarised without losing detail?
5. **A web failure with a screenshot.** The prototype's screenshot choice (last screenshot at or before the failing step) is untested against a real UI failure.

## Next steps

1. Run the prototype against a failing browser test (Selenium and Playwright) and a JUnit 4 project, and check the screenshot selection.
2. Decide the writer (open question 1) and the Cucumber data (open question 2).
3. Implement in Java behind a schema test, and then test with an agent: give it the summary instead of console output for a set of seeded failures, and measure steps to a correct fix.
