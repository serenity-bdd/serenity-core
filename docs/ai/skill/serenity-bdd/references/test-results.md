# Reading Test Results Without the HTML Report

Verified against Serenity **5.4.1** by running a passing and a failing project (`results-checks/verify.py`).

An AI assistant, a script or a CI step can read the outcome of a run from files, not from the HTML report or the Maven console. Serenity writes them to `target/site/serenity/`.

## What is written, and when

| File | Written | Contains |
|---|---|---|
| `<64 hex characters>.json`, one per test | **While the tests run.** It exists after a failed build, when no HTML report was generated | The full outcome of one test: title, result, requirement path, the step tree, and the failure |
| `SERENITY-JUNIT-<same 64 hex characters>.xml`, one per test | While the tests run | The same test in standard JUnit XML, for CI tools |
| `index.html`, the other `.html` pages, `summary.txt` | **Only if the report was generated.** `mvn verify` stops at a failed test, so after a failure they are missing (see the troubleshooting page) | The HTML report; `summary.txt` has the totals as prose |
| `serenity-summary.json` | Only if the optional JSON summary report is enabled (below) | Totals and one entry per scenario |

The JSON file name is a 64-character hash. It was the same on every run of the same test, but it tells you nothing about the test, so read the `title`, `testCaseName` and `methodName` fields inside.

So after any run, including a failed one, **read `target/site/serenity/*.json`**. Do not wait for `index.html` and do not scrape it.

## The quickest way: `scripts/results.py`

The script is in the skill (`scripts/results.py`) and in the serenity-core repository at `docs/ai/skill/serenity-bdd/scripts/results.py`; copy it into the project if the skill is not installed.

```bash
python3 <skill>/scripts/results.py target/site/serenity          # totals, a line per test, details of each failure
python3 <skill>/scripts/results.py target/site/serenity --json   # the same as one JSON document
```

It prints the totals, one line per test (result, title, requirement path, outcome file), and for each failed test the failing step, the exception type and message, the first stack frame in the project's own code, and the outcome file to open for the full detail. The exit code is 1 if any test failed, errored or was compromised, and 2 if there are no outcome files. A run of more than 40 tests lists only the tests that did not pass unless you add `--all`. It reads only the JSON files, uses no dependencies, and is a different tool from `outline.py` (which judges the wording and structure of the report, not the result of the run).

When the failure is an assertion or a wait (for example Screenplay `WaitUntil` or `should(seeThat(..))`), Serenity may not mark any step as failed. The script then says so and gives the last step recorded; the stack frame names the line of your test that failed.

## The fields of an outcome file

```text
title, name          the scenario title as the report shows it
result               SUCCESS, FAILURE, ERROR, COMPROMISED, PENDING, SKIPPED, IGNORED ...
testCaseName         class name; methodName: the test method (JUnit)
userStory            { path, storyName, ... }  the requirement it belongs to (package path or feature file)
featureTag, tags     the feature and any @tags
startTime, duration  duration in milliseconds
testSource           JUnit5, Cucumber ...
testSteps            the step tree: [ { number, description, result, duration, children: [ ... ] } ]
```

For a test that did not pass, the file also has:

```text
testFailureCause     { errorType, message, stackTrace: [ { declaringClass, methodName, fileName, lineNumber } ] }
testFailureClassname the exception class
testFailureMessage   the exception message
testFailureSummary   "FAILURE;<class>;<message>;<file>"
```

and the step that failed (when one is marked) has `exception` with the same `errorType`, `message` and `stackTrace`. REST steps carry `restQuery` with `method`, `path`, `requestHeaders`, `content`, `statusCode`, `responseHeaders` and `responseBody`, which is often all that is needed to see why an API test failed. Nested steps are in `children`.

The step tree is long for big tests (every request, with its response body, is in it). Read the summary first and open one outcome file only for a test you are investigating.

## Optional: a JSON summary of the whole run

The `serenity-json-summary-report` artifact writes one `serenity-summary.json` for the run. It is not enabled by default. Add it as a dependency of the Serenity Maven plugin and ask for the report:

```xml
<plugin>
  <groupId>net.serenity-bdd.maven.plugins</groupId>
  <artifactId>serenity-maven-plugin</artifactId>
  <version>${serenity.version}</version>
  <dependencies>
    <dependency>
      <groupId>net.serenity-bdd</groupId>
      <artifactId>serenity-json-summary-report</artifactId>
      <version>${serenity.version}</version>
    </dependency>
  </dependencies>
  <!-- keep your existing executions -->
</plugin>
```

```bash
mvn serenity:reports -Dserenity.reports=json-summary
```

`serenity-summary.json` has `report`, `results` (`counts`: total, success, failure, error, pending, skipped ...; durations), `resultsByFeature` (each feature with its scenarios: `title`, `result` and for each outcome an `errorMessage`), `frequentFailures`, `unstableFeatures`, `coverage` and `tags`. It has the failure message of each scenario but not the failing step, the stack trace or the link to the outcome file, so for investigating a failure use the outcome files (or `results.py`). The goal ran after a failed build and produced it from the outcome files, with no HTML report.

## What this is not

- The outcome file format is Serenity's own. It is stable in practice but is not a published contract, so read the fields listed above and ignore the rest.
- The JUnit XML (`SERENITY-JUNIT-*.xml`) and Maven's `target/surefire-reports/*.xml` are the standard formats; use them when a tool expects JUnit XML.

## What an assistant should do after a run

1. Run the build (`mvn verify`). If it fails, do not stop at the console output.
2. Run `results.py` on `target/site/serenity`. Fix compile errors from the console first; they produce no outcome files.
3. For each failed test, read the message and the stack frame in your own code, and decide whether the test or the application is wrong. If the application is wrong, say so rather than editing the test to pass.
4. When all tests pass, build the report (`mvn verify` does it when the tests pass) and review it with `outline.py` and the review checklist.
