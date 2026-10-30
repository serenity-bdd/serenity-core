# Reviewing Serenity tests and their living documentation

Used by the `serenity-test-reviewer` agent after tests are written, and by anyone reviewing Serenity tests. Style-neutral: it applies to Page Objects, Screenplay and Cucumber, Selenium, Playwright and REST. Do not ask for a different style; judge the tests inside the style they use.

Read `living-documentation.md` first for what Serenity does with titles, steps and structure.

## What to look at

1. The test sources (and the feature files).
2. The report as a reader sees it: run the tests, then `python3 <skill>/scripts/outline.py <project>/target/site/serenity`. If the tests cannot run here, review the sources and say the report was not seen.
3. The task the user asked for, to check that the tests cover it.

## The checks

Each check is PASS, WEAK or FAIL. Quote the line or title, and write the replacement.

### A. Intent: does a reader learn the business rule?
- **A1** The scenario title states the rule or outcome in business words (not `testCheckout1`, not `shouldReturn201`, not a description of clicks).
- **A2** The scenario is about one behaviour. A title with "and" covering two rules should be two scenarios.
- **A3** It is clear why the behaviour matters or what the user gets (a title like "A reader sees an order number after paying" says it; "Checkout works" does not).
- **A4** Cucumber: the feature has a short business description; scenario outlines use meaningful example column names.

### B. Steps: business language
- **B1** Steps read as a narrated story with who/what, in the project's domain terms.
- **B2** No selectors, ids, URLs, sleeps, driver calls or HTTP codes in step text, titles or the test body (they belong inside page objects, tasks, step libraries, step definitions). In a REST test, a status code is fine inside the check, not as the step name.
- **B3** The final step/check states the business expectation ("the confirmation shows an order number"), and the check really proves that (see D1).
- **B4** One level of abstraction per scenario: business tasks at the top, mechanics inside them.

### C. Organisation: easy to navigate
- **C1** Scenarios are grouped by function (package/folder per capability, one class or feature file per feature, or `@Epic/@Feature/@Story`), not one big class and not by technical layer.
- **C2** The report tree (from `outline.py`) reads like a table of contents of what the application does. Names are business phrases, with no class name showing ("When signing in test": the class needs `@DisplayName`) and no duplicates.
- **C3** One grouping method for the whole project (not package structure mixed with `@Feature` annotations).
- **C4** Description files exist where an area needs explaining (Cucumber `readme.md`, or `src/test/resources/stories/<package path>/readme.md`) and are short and about the business.

### D. Test quality
- **D1** The assertions check what the title claims, with specific expected values from the task (not "page loaded", not `isNotNull`, not the same value read back from the test's own input). Ask: if the application broke in the way the title describes, would this test fail?
- **D2** No fixed sleeps. Waits are on a condition (Serenity waits, `WaitUntil`, Playwright auto-wait).
- **D3** Scenarios are independent: no shared mutable state between tests, no dependence on order, no static fields for scenario state.
- **D4** Test data is visible where it matters (the value a rule depends on is in the scenario, not hidden in a helper), and secrets are not hard-coded beyond what the task gave.
- **D5** No duplicated flows: repeated step sequences are a task, step method or Background.
- **D6** It follows the project's existing style, driver and runner, and does not mix styles in one test. The project builds and the tests were run or the reason is stated.
- **D7** Covers what was asked, including the negative case when the task mentioned one, and nothing invented beyond the application's described behaviour.

## Output

```
VERDICT: READY | NEEDS CHANGES

LIVING DOCUMENTATION AS A READER SEES IT
<the outline, trimmed>

FINDINGS (worst first)
1. [FAIL B2] src/test/java/.../CheckoutTest.java:31: step "Click #place-order" shows a selector
   Change to: Task.where("{0} places the order", ...)
...

WHAT IS GOOD
<one or two lines>
```

READY means no FAIL and at most two WEAK, and the report was seen. Be specific and brief: every finding says where it is and what to write instead. Do not edit files; the author applies the changes, and a second review follows if there were FAILs.
