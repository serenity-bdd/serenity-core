# Serenity troubleshooting by symptom

Verified against Serenity **5.4.1** (Playwright modules: **5.3.11**). Each entry below was reproduced in a test project and its fix was run; `../../troubleshooting/repro.py` re-runs all of them and `../../troubleshooting/observed.json` holds the exact messages. Entries not reproduced are listed at the end.

How to use this page: find the message you see (the text is quoted from the real output), read the cause, apply the fix, and run the build again. If nothing here matches, read the first `Caused by:` and the first `[ERROR]` line in the Maven output, not the last.

## Where to look first

- Serenity writes one JSON file per test to `target/site/serenity/` as the tests run. If the HTML report is missing, these files still show what happened.
- The line `Results: N tests | ...` in the Maven output (from the Serenity Maven plugin) tells you how many tests Serenity saw.
- `mvn -q test-compile` separates compile errors from run-time ones.

## The build fails or does nothing

### "The wrapped NoClassDefFoundError is likely caused by the versions of JUnit jars ... not being properly aligned"

```
The following conflicting versions were detected:
- org.junit.jupiter.api: 6.1.3
- org.junit.platform.suite.api: 1.12.2 ...
```

- **Cause:** the pom pins `junit-jupiter`, `junit-platform-suite` or another JUnit artifact to a version other than the one Serenity brings. Serenity 5.4.1 depends on **JUnit 6.1.3**. The code compiles; the tests fail when they start.
- **Fix:** remove the pinned JUnit versions and import the JUnit BOM so all JUnit artifacts match:

```xml
<dependencyManagement>
  <dependencies>
    <dependency><groupId>org.junit</groupId><artifactId>junit-bom</artifactId><version>6.1.3</version><type>pom</type><scope>import</scope></dependency>
  </dependencies>
</dependencyManagement>
```

List `junit-jupiter` and `junit-platform-suite` without versions.

### `java.lang.AbstractMethodError: Receiver class io.cucumber.junit.platform.engine.CucumberConfiguration does not define or inherit an implementation of the resolved method 'abstract ... getGlueDiscoveryRequest()'`

- **Cause:** `cucumber-java` or `cucumber-junit-platform-engine` is pinned to a version that does not match the Cucumber that `serenity-cucumber` brings (8.0.4 for Serenity 5.4.1; 7.34.2 for 5.3.11). Reproduced with 7.31.0.
- **Fix:** use the version Serenity brings (check with `mvn dependency:tree -Dincludes=io.cucumber`), or leave the Cucumber versions out.

### `Failed to transform configuration parameter with key 'cucumber.plugin' and initial value 'io.cucumber.core.plugin.SerenityReporterParallel,pretty'`

- **Cause:** the Serenity plugin class is named with the wrong package.
- **Fix:** `@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel,pretty")`.

### The build succeeds but `Results: 0 tests | 0 passed | ...`, no Cucumber scenarios ran, and the report is empty

```
[INFO]   - Results: 0 tests | 0 passed | 0 failed | 0 errors | 0 compromised | 0 pending | 0 aborted
```

- **Cause:** Maven's test plugin only runs classes whose names match its default patterns (`*Test`, `*Tests`, `Test*`, `*TestCase`). A Cucumber `@Suite` class called, for example, `CucumberSuite` or `CucumberTestSuite` is skipped without any warning. Reproduced with `CucumberSuite`.
- **Fix:** name the runner `RunCucumberTests` (or any name ending in `Test` or `Tests`), or add the name to `<includes>` in the surefire plugin configuration.

## The report is missing or wrong

### Tests failed, and there is no `target/site/serenity/index.html`

- **Cause:** the report is built by the Serenity Maven plugin in the `post-integration-test` phase, which Maven never reaches when tests fail. The per-test JSON files exist, but no HTML. Reproduced: after a failing scenario, `mvn verify` printed `There are test failures`, and `index.html` was absent.
- **Fix:** either run the report goal after the failed build:

```
mvn net.serenity-bdd.maven.plugins:serenity-maven-plugin:5.4.1:aggregate
```

(this produced `index.html`), or let the build continue past test failures with `-Dmaven.test.failure.ignore=true` (then the exit code is 0 even when tests fail, so a CI job must check results another way).

### `Conflicting module versions. Module [groovy-xml is loaded in version 4.0.29 and you are trying to load version 3.0.9` / `Could not initialize class io.restassured.RestAssured`

- **Cause:** the pom pins REST-assured (`io.rest-assured:*`) or Groovy (`org.codehaus.groovy:groovy*`, Groovy 3) versions left over from an older Serenity. Serenity 5.4.1's REST support uses Groovy 4. Reproduced with REST-assured 4.5.1 and Groovy 3.0.9.
- **Fix:** delete those dependencies and let `serenity-rest-assured` bring its own. Seen when upgrading a real Serenity 2.4 project, which then passed 3 of 3 scenarios.

### The report shows more tests, or old failures, than the run had

- **Cause:** Serenity counts every outcome file in `target/site/serenity`, including ones left by earlier runs. Reproduced: after a full run (5 scenarios), a run filtered to 1 scenario, without cleaning, reported `Results: 5 tests`; after the directory was deleted it reported 1 (`../../cucumber-checks/verify.py`, case `stale-outcomes-counted`).
- **Fix:** start from a clean directory: `mvn clean verify`, or delete `target/site/serenity` (Gradle: `gradle clean test`, whose `clean` also clears it).

### "No BaseStepListener has been registered - are you running your test using the Serenity runners?"

```
java.lang.NullPointerException: No BaseStepListener has been registered - are you running your test using the Serenity runners?
```

- **Cause:** code that uses Serenity's reporting or Screenplay (for example `Serenity.recordReportData()`, an actor performing a task) runs outside a Serenity-managed test. In Cucumber the usual reason is that the Serenity plugin is not in the runner's `PLUGIN_PROPERTY_NAME`. Reproduced by removing the plugin.
- **Fix:** in Cucumber, add `net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel` to the plugins. In a JUnit 5 test, add `@ExtendWith(SerenityJUnit5Extension.class)`.

### A value defined in `serenity.conf` reads as `null`

- **Cause (two common ones):** (1) the value is under `environments { }`, and `environmentVariables.getProperty("app.url")` never returns those; (2) the file is not where Serenity looks: `serenity.conf` is read from `src/test/resources`, not the project root. Reproduced both (`../../config-checks/verify.py`).
- **Fix:** (1) `EnvironmentSpecificConfiguration.from(env).getOptionalProperty("app.url")`; (2) move the file to `src/test/resources/serenity.conf` and run `mvn clean`.

### Filtering by tag does nothing under Gradle: all scenarios still run

- **Cause:** Gradle does not pass `-D` options on the command line to the tests' JVM, so `gradle test -Dcucumber.filter.tags=@smoke` is ignored. Reproduced: 4 outcomes with the option, unchanged, and 1 once the build forwarded it.
- **Fix:** forward the option in `build.gradle`, as in the Cucumber page: `systemProperties System.properties.findAll { k, v -> k.toString().startsWith('cucumber.') }` inside `test { }`.

## The browser

### `NullPointerException: Cannot invoke "org.openqa.selenium.WebDriver.get(String)" because "this.driver" is null`

- **Cause:** a `@Managed WebDriver driver;` field in a **step library** (or any class other than the test class). `@Managed` only fills the field in the test class; elsewhere it stays null. Reproduced in a step library.
- **Fix:** extend `net.serenitybdd.core.steps.UIInteractionSteps` and use `getDriver()` (or use a page object, which gets the driver itself). Keep `@Managed` for the test class.

### `DriverConfigurationError: Unsupported browser type: chromee. Did you mean firefox?`

- **Cause:** `webdriver.driver` (system property or `serenity.conf`) names a browser Serenity does not know. Reproduced with a misspelling.
- **Fix:** use a supported name: `chrome`, `firefox`, `edge`, `safari`, `iexplorer`, `appium`, `remote`, ... (in this case the message guessed `firefox` for a misspelt `chrome`, so read the list rather than trusting the suggestion).

### `DriverConfigurationError: After several attempts, could not instantiate new WebDriver instance of type class org.openqa.selenium.chrome.ChromeDriver (Unable to obtain: chromedriver, error chromedriver must exist: /no/such/chromedriver`

- **Cause:** `webdriver.chrome.driver` points to a file that does not exist (a stale path from another machine or an old setup).
- **Also seen as:** `chromedriver must be executable: src/test/resources/webdriver/mac/chromedriver`, from an old `drivers { mac { webdriver.chrome.driver = "..." } }` block with a bundled binary (found upgrading a real Serenity 2.1 project; removing the block let Chrome start).
- **Fix:** remove the property so Selenium downloads or finds a matching driver by itself, or set it to a real path. Do not commit machine-specific absolute paths; if a driver must be supplied, see the OS-specific `drivers` settings in the driver configuration docs.

### `no such element: Unable to locate element: {"method":"xpath","selector":"input.new-todo"}` (Screenplay with Selenium)

- **Cause:** `Target.the(..).locatedBy("input.new-todo")`. Serenity reads the string as XPath when it compiles as an XPath expression, and `input.new-todo` does (a name test). So the element is searched as XPath and not found. `.new-todo`, `#id` and `[data-x=y]` are read as CSS.
- **Fix:** `Target.the(..).located(By.cssSelector("input.new-todo"))`, or a CSS selector that starts with `.`, `#` or `[`.

### `NoClassDefFoundError: io/cucumber/core/eventbus/EventBus` (and similar) after raising the Serenity version

- **Cause:** the project still pins an older Cucumber (4.x) and excludes `cucumber-core` from Serenity, as old starter projects did. Seen upgrading a real Serenity 2.1 project; not reproduced in the test projects here. After the pin and the exclusion were replaced by Cucumber 8.0.4 the project built; remember `mvn clean`, because old compiled classes can hide the next error (see the upgrading reference).
- **Fix:** use the Cucumber version Serenity brings (8.0.4 for 5.4.1) and remove the exclusion.

## Not reproduced here

These come up often in public questions. They are not covered by a tested fix, so use the driver configuration and Maven documentation, and do not rely on advice you cannot check:

- `Chrome failed to start: exited normally` / `session not created` after a Chrome update, `DevToolsActivePort file doesn't exist`, and browser start-up problems inside Docker or on Jenkins (they depend on the machine, user and container).
- Dependency convergence errors and `Could not resolve dependencies` (they depend on the project's other dependencies).
- `NoClassDefFoundError` after upgrading from Serenity 3 to 4 (for example `net.thucydides.model.util.EnvironmentVariables`): classes moved between packages. The upgrade guide in the documentation lists them.
- Slow report aggregation, wrong counts and reports from parallel runs.
