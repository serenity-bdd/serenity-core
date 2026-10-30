# Serenity with Cucumber (JUnit Platform)

Verified against Serenity **5.4.1**: Cucumber **8.0.4**, JUnit **6.1.3**. Every command on this page was run: `../../cucumber-checks/verify.py` runs them against the example in `../examples/cuke-plain` (a browser-free example with 5 scenarios) and checks how many scenarios ran. Cucumber with Screenplay and a browser is on the Screenplay page; this page is about Cucumber itself and applies to both.

## 1. Setup

- **Maven:** the Cucumber row of the dependency table on the project-setup page: `serenity-core`, `serenity-junit5`, `serenity-cucumber`, `io.cucumber:cucumber-java`, `io.cucumber:cucumber-junit-platform-engine`, `junit-platform-suite`. Use the JUnit BOM, and use the Cucumber version Serenity brings.
- **Gradle:** `cucumber-checks/gradle/build.gradle` is a working build (below).
- Feature files go in `src/test/resources/features`, step definitions and hooks in a package under `src/test/java`.

## 2. The runner

A JUnit Platform suite. No `@RunWith`:

```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "demo")   // the package(s) with steps and hooks
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel,pretty")
public class RunCucumberTests {
}
```

- The plugin class is `net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel`. It connects Cucumber to Serenity: without it, Serenity APIs used in steps fail with "No BaseStepListener has been registered".
- Name the class so that Maven's test plugin runs it: ending in `Test` or `Tests` (`RunCucumberTests`). A name such as `CucumberSuite` is skipped silently (see the troubleshooting page, "Results: 0 tests").
- `@RunWith(CucumberWithSerenity.class)` (from `net.serenitybdd.cucumber`) is the JUnit 4 style runner. In a JUnit 5 project use the suite above.

## 3. Running part of the suite

All of these were run against the example. Add them to the Maven command line:

| Goal | Option | Scenarios run (of 5) |
|---|---|---|
| Only `@smoke` | `-Dcucumber.filter.tags=@smoke` | 1 |
| Everything except `@wip` | `-Dcucumber.filter.tags="not @wip"` | 4 |
| Tag expression | `-Dcucumber.filter.tags="@basket and not @wip"` | 1 |
| By scenario name (regular expression) | `-Dcucumber.filter.name="Adding passes"` | 1 |
| One feature file | `-Dcucumber.features=src/test/resources/features/basket.feature` | 2 |

The same filters can be fixed in the project instead of the command line:

- in the runner: `@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @wip")` (`Constants.FILTER_TAGS_PROPERTY_NAME`, the same class as `GLUE_PROPERTY_NAME`): 4 scenarios, or
- in `src/test/resources/junit-platform.properties`: `cucumber.filter.tags=not @wip`: 4 scenarios.

When `cucumber.features` is given, Cucumber ignores the runner's `@SelectClasspathResource` and prints a warning saying so; that is expected.

**Re-running failed scenarios.** Add the Cucumber `rerun` plugin to the runner (`...SerenityReporterParallel,pretty,rerun:target/rerun.txt`). After a run that has failures, `target/rerun.txt` lists them (`classpath:features/failing.feature:3`). Run only those with `-Dcucumber.features=@target/rerun.txt` (verified: 6 scenarios, then 1).

## 4. Gradle

`cucumber-checks/gradle/build.gradle` (Serenity Gradle plugin `net.serenity-bdd.serenity-gradle-plugin` 5.4.1, Gradle 9.8.1 tested, tasks `test` and `aggregate`) was run: `gradle clean test` ran the 5 scenarios and `aggregate` produced `target/site/serenity/index.html`.

**Gradle does not pass `-D` options to the test JVM.** `gradle test -Dcucumber.filter.tags=@smoke` runs every scenario unless the build forwards the option (verified: 4 outcomes without it, 1 with it):

```groovy
test {
    useJUnitPlatform()
    systemProperties System.properties.findAll { k, v -> k.toString().startsWith('cucumber.') }
    finalizedBy 'aggregate'
}
```

The same applies to any other `-D` option your tests read. `ignoreFailures = true` in that file lets the report task run when a scenario fails (the same idea as `-Dmaven.test.failure.ignore=true` for Maven); leave it out if the build must fail.

## 5. Scenario outlines, data tables and parameter types

`../examples/cuke-plain/features/maths.feature` has a `Scenario Outline` with an `Examples` table (2 rows) and `basket.feature` has a `Background` and a data table. In the steps (`BasketSteps.java`):

- a data table argument is `io.cucumber.datatable.DataTable` (`items.asMaps()` gives a list of rows);
- a custom parameter type is a method annotated `@ParameterType("\\d+\\.\\d+")` that returns the type (`{money}` in the step text gives a `BigDecimal`);
- Maven counts each example row as a scenario (the example has 5: 1 + 2 + 2); the Serenity outcome files (`target/site/serenity/*.json`) count the outline once (4 files for these 5 scenarios), so two counts of the same run can differ.

## 6. Hooks

`../examples/cuke-plain/demo/Hooks.java`:

- `@Before` and `@After` from `io.cucumber.java`; they take an optional `io.cucumber.java.Scenario` argument (`getName()`, `getSourceTagNames()`, `isFailed()`, `getStatus()`).
- `@Before("@basket")` runs only for scenarios with that tag; `order` sorts hooks: `@Before` hooks run from the lowest order to the highest, `@After` hooks from the highest to the lowest (verified: before-1, before-5, after-5, after-1).
- `Serenity.recordReportData().withTitle("..").andContents("..")` adds data to the scenario in the Serenity report (checked: it appears in the outcome files).

## 7. Sharing state between steps

Cucumber creates new step-definition objects for every scenario. Three ways to share state inside one scenario:

1. **Fields of one step class.** Fine when all the steps are in that class.
2. **Serenity session variables** (used in the example): `Serenity.setSessionVariable("basketTotal").to(value)` and `Serenity.sessionVariableCalled("basketTotal")`. They are per scenario (and per thread) and work from any class.
3. **Dependency injection with PicoContainer:** add `io.cucumber:cucumber-picocontainer` (same version as Cucumber), give each step class a constructor that takes the shared object, and Cucumber creates one instance per scenario (verified with two step classes sharing a `Shared` object).

Do not use static fields to share scenario state: with parallel execution scenarios overwrite each other.

## 8. Parallel execution

`src/test/resources/junit-platform.properties`:

```
cucumber.execution.parallel.enabled=true
cucumber.execution.parallel.config.strategy=fixed
cucumber.execution.parallel.config.fixed.parallelism=4
```

Verified: the 5 scenarios ran and the report was produced. Keep in mind that each scenario needs its own state (section 7) and, in a web test, its own browser. Reports of parallel runs are a common source of problems; the troubleshooting page says what has and has not been reproduced.

## 9. Cucumber with Screenplay

Put `OnStage.setTheStage(new OnlineCast())` in a `@Before` hook and a `@ParameterType` named `actor` that returns `OnStage.theActorCalled(name)`, as in the Screenplay page's Cucumber example.

## 10. Cucumber with page objects and a step library (no Screenplay)

The glue code is thin: each step definition calls one `@Step` method of a step library, and the step library uses a page object. The example `../examples/cuke-pages` has the whole chain (`SignInStepDefinitions` -> `ReaderSteps` -> `SignInPage`) and its 3 scenarios pass in headless Chrome; `../../cucumber-checks/verify_pages.py` runs the claims below.

- Declare the library in the step-definition class with **`@Steps ReaderSteps reader;`** (`net.serenitybdd.annotations.Steps`). Declare the page object as a plain field of the step library (`SignInPage signInPage;`); Serenity creates it and gives it the driver. With `ReaderSteps reader = new ReaderSteps();` the page field stays null and the scenario errors with `NullPointerException: ... "this.signInPage" is null`.
- Page objects extend `net.serenitybdd.core.pages.PageObject` and use `@FindBy` with `WebElementFacade`; no `@Managed` is needed in Cucumber. See `page-objects.md` for the idioms.
- The report shows both layers: the Gherkin line ("When Reader signs in as ...") with the `@Step` text nested under it ("Sign in as reader@example.com with the password wrong"). Write the `@Step` text in business language too.
- The runner and plugin are the same as in section 2: the plugin class is `net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel`.

### Optional text `book(s)` in a Cucumber expression

In a step written `@Then("the catalogue lists {int} book(s)")`, `(s)` is **optional text**: the step matches "5 book" and "5 books". It does **not** match a feature line that contains the characters `book(s)`. Reproduced: a feature saying `the catalogue lists 5 book(s)` gave `The step 'the catalogue lists 5 book(s)' is undefined.` and Maven failed the build, while the Serenity report shows the scenario as **pending** (not failed), so look at the console output as well as the report. Write the feature in plain words ("1 book", "5 books"); to match a literal `(s)`, escape it in the step as `book\\(s)`.

## 11. Not covered here

Gherkin in other languages, `@Before` for browser start-up in cloud grids, the Cucumber `Transpose`/`DocString` types, Spring or Guice object factories, and Cucumber-JVM versions other than the one Serenity brings.
