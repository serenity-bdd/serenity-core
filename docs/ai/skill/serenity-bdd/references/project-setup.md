# Serenity project setup

Verified against Serenity **5.4.1**. The dependency sets, the JUnit BOM and the report plugin were run end to end: `mvn clean verify` ran the example tests and produced `target/site/serenity/index.html`. The `serenity.conf` snippet follows the project's own samples but was not run (headless mode was set on the command line).

Use one Serenity version for every `net.serenity-bdd` artifact (a `serenity.version` property). Java 17+. Playwright modules are published up to 5.3.11, so a Playwright project may need `serenity.version` 5.3.11 for those artifacts; check Maven Central for the latest before pinning.

## Dependencies by style (all `test` scope)

| Style | `net.serenity-bdd` artifacts | Other |
|---|---|---|
| Page Objects + Selenium | `serenity-core`, `serenity-junit5` | `org.junit.jupiter:junit-jupiter`, `org.assertj:assertj-core` |
| Page Objects + Playwright | `serenity-core`, `serenity-junit5`, `serenity-playwright` | + `com.microsoft.playwright:playwright` |
| Screenplay + Selenium | `serenity-core`, `serenity-junit5`, `serenity-screenplay`, `serenity-screenplay-webdriver`, `serenity-ensure` | JUnit Jupiter, AssertJ |
| Screenplay + Playwright | `serenity-core`, `serenity-junit5`, `serenity-screenplay`, `serenity-screenplay-playwright`, `serenity-playwright` | JUnit Jupiter, `com.microsoft.playwright:playwright` |
| Screenplay + REST | `serenity-core`, `serenity-junit5`, `serenity-screenplay`, `serenity-screenplay-rest`, `serenity-rest-assured`, `serenity-ensure` | JUnit Jupiter |
| Cucumber (+ Screenplay) | the Screenplay web set above, plus `serenity-cucumber` | `io.cucumber:cucumber-java`, `io.cucumber:cucumber-junit-platform-engine`, `org.junit.platform:junit-platform-suite` |

Serenity 5.4.1 itself depends on **JUnit 6.1.3** (`serenity-junit5`) and **Cucumber 8.0.4** (`serenity-cucumber`). Don't pin older JUnit or Cucumber versions: mixed JUnit versions fail at run time with "conflicting versions" errors, even though the code compiles. Import the JUnit BOM so every JUnit artifact (including `junit-platform-suite` and the engine that `cucumber-junit-platform-engine` brings in) has the same version:

```xml
<dependencyManagement>
  <dependencies>
    <dependency><groupId>org.junit</groupId><artifactId>junit-bom</artifactId><version>6.1.3</version><type>pom</type><scope>import</scope></dependency>
  </dependencies>
</dependencyManagement>
```

Then list `junit-jupiter` and `junit-platform-suite` without versions, and use Cucumber 8.0.4 for `cucumber-java` and `cucumber-junit-platform-engine`. Other versions in the harness: Playwright 1.52.0, AssertJ 3.27.7. Add `org.slf4j:slf4j-simple` to silence logging warnings.

Reference poms: `docs/ai/eval/harness/h-*/pom.xml`.

## Build and reports

```xml
<plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-surefire-plugin</artifactId><version>3.5.1</version></plugin>
<plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-failsafe-plugin</artifactId><version>3.5.1</version></plugin>
<plugin>
  <groupId>net.serenity-bdd.maven.plugins</groupId>
  <artifactId>serenity-maven-plugin</artifactId>
  <version>${serenity.version}</version>
  <executions>
    <execution><id>serenity-reports</id><phase>post-integration-test</phase><goals><goal>aggregate</goal></goals></execution>
  </executions>
</plugin>
```

- Compile only: `mvn test-compile` (fast, use it to check generated code).
- Run and build the report: `mvn clean verify`. The report is in `target/site/serenity/index.html`.
- Name test classes so surefire/failsafe pick them up (`*Test`, or `*IT` for failsafe); a Cucumber runner is a JUnit Platform `@Suite` class.

## `src/test/resources/serenity.conf` (HOCON)

```hocon
serenity {
  project.name = "My project"
  take.screenshots = FOR_FAILURES      # FOR_EACH_ACTION, BEFORE_AND_AFTER_EACH_STEP, DISABLED
}
headless.mode = true
webdriver.base.url = "https://example.com"
environments {
  default { base.url = "https://example.com" }
}
```

`serenity.*` and `thucydides.*` keys are synonyms; prefer `serenity.*`. Don't invent keys: check `docs/reference` on https://serenity-bdd.github.io or `ThucydidesSystemProperty` in `serenity-model`.
