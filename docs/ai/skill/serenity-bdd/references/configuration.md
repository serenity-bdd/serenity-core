# Serenity configuration: `serenity.conf`, properties and environments

Verified against Serenity **5.4.1**. The behaviour described here was run by `../../config-checks/verify.py` against the example in `../examples/config`: 21 checks, covering where the files are read from, which source wins, environments, command-line overrides, reading values in code, and what the screenshot settings do. Keys are checked against the `ThucydidesSystemProperty` enum in the jar. The meaning of a key comes from the Serenity documentation (the reference page of properties), unless the text says it was run.

## 1. Where configuration comes from

| Source | Where | Read? |
|---|---|---|
| `serenity.conf` (HOCON) | `src/test/resources/` (on the classpath) | yes |
| `serenity.conf` | project root | **no, ignored** |
| `serenity.properties` | project root | yes |
| `serenity.properties` | `src/test/resources/` | yes |
| System properties | `-Dkey=value` on the Maven command line | yes, override the files |

What wins:

- A `-D` option wins over both files (`-Dwebdriver.base.url=...` replaced the value in the file).
- `serenity.properties` in the **project root** wins over `serenity.conf` in `src/test/resources`.
- `serenity.conf` wins over a `serenity.properties` that is also in `src/test/resources`.
- OS environment variables (`SERENITY_PROJECT_NAME=...`) did **not** set `serenity.project.name`.

`thucydides.*` and `serenity.*` are synonyms: `thucydides.project.name = "x"` in either file sets the project name. Prefer `serenity.*`.

After deleting or moving a configuration file, run `mvn clean ...`: Maven leaves the old copy in `target/test-classes`, so a deleted `serenity.conf` is still read (checked: the old value was still used without `clean`).

## 2. `serenity.conf`

HOCON. Nested and dotted forms mean the same:

```hocon
serenity {
  project.name = "My project"
  take.screenshots = FOR_FAILURES
}
webdriver {
  base.url = "https://example.com"
}
headless.mode = true
```

The full example, with environments, is `../examples/config/resources/serenity.conf`.

## 3. Environments

```hocon
environments {
  default { app.url = "https://dev.example.com"
            db.name = "dev" }
  staging { app.url = "https://staging.example.com" }
  all     { company = "Acme" }
}
```

Pick one with `-Denvironment=staging`.

- With no `environment`, the `default` block applies. With `-Denvironment=staging`, `staging` applies and values it does not define fall back to `default` (`db.name` stayed `dev`).
- `all` applies in every environment.
- **Your own keys inside `environments { }` are NOT visible as plain properties.** `environmentVariables.getProperty("app.url")` returns **null**, in every environment. Read them through `EnvironmentSpecificConfiguration` (next section). This is the usual cause of "I get null when I read a value from serenity.conf".
- **Serenity's own keys inside `environments { }` do work** and override the top-level value: `webdriver.base.url` and `serenity.project.name` inside `environments { staging { } }` took effect with `-Denvironment=staging`. Values in `default` override the top-level value too.
- Command-line override of a value in `environments`: use the full name, `-Denvironments.staging.app.url=https://...`. A plain `-Dapp.url=...` did **not** override it.

## 4. Reading values in code

```java
EnvironmentVariables env = SystemEnvironmentVariables.currentEnvironmentVariables();   // net.thucydides.model.environment
String name = ThucydidesSystemProperty.SERENITY_PROJECT_NAME.from(env);                 // net.thucydides.model
String url  = EnvironmentSpecificConfiguration.from(env).getOptionalProperty("app.url").orElse("none"); // net.serenitybdd.model.environment
```

- Works anywhere, including static code and static initialisers. `Serenity.environmentVariables()` (`net.serenitybdd.core.Serenity`) returns the same kind of object.
- In a step library (a class with `@Step` methods used through `@Steps`), declare a field `EnvironmentVariables environmentVariables;` (type `net.thucydides.model.util.EnvironmentVariables`): Serenity injects it.
- Built-in keys: `ThucydidesSystemProperty.X.from(env)` (also `integerFrom`, `booleanFrom`, `isDefinedIn`) or `env.getProperty("webdriver.base.url")`.
- Your own keys outside `environments { }` can be read with `env.getProperty("my.key")`.

## 5. Common keys

All of these exist in `ThucydidesSystemProperty` (checked). Meanings are from the Serenity documentation, except where marked *(run)*.

| Key | Meaning |
|---|---|
| `webdriver.driver` | Which browser to run in (`chrome`, `firefox`, `edge`, `safari`, ...). An unknown name fails with `Unsupported browser type` *(run)*. |
| `webdriver.base.url` | The default starting URL; base for relative paths. Read back *(run)*. |
| `headless.mode` | Headless Chrome, Firefox or Edge. `true` added Chrome's `--headless` arguments *(run)*. |
| `webdriver.chrome.driver` | Path to a chromedriver binary. Only set it if you really supply one; a missing file is an error *(run)*. |
| `webdriver.autodownload` | `false` stops Serenity downloading driver binaries. |
| `webdriver.remote.url`, `webdriver.remote.driver` | The URL of a Selenium Grid or cloud hub, and the driver to use there. |
| `webdriver.timeouts.implicitlywait` | How long the driver waits for elements to appear, in milliseconds. |
| `webdriver.wait.for.timeout` | How long fluent waits wait, in milliseconds. |
| `serenity.project.name` | The name shown on reports. Read back *(run)*. |
| `serenity.outputDirectory` | Where reports go (default `target/site/serenity`). |
| `serenity.take.screenshots` | `FOR_EACH_ACTION`, `BEFORE_AND_AFTER_EACH_STEP`, `AFTER_EACH_STEP`, `FOR_FAILURES` or `DISABLED`. On a passing test `DISABLED` and `FOR_FAILURES` stored no screenshots; the other values, and not setting it, stored some *(run)*. |
| `serenity.browser.width`, `serenity.browser.height` | Browser window size. |
| `serenity.restart.browser.frequency` | Start a new browser every N rows in data-driven tests. |
| `serenity.reports.show.step.details` | Show a breakdown of steps by result in report tables. |

Do not invent keys. If a key is not in the Serenity documentation or `ThucydidesSystemProperty`, it is probably ignored without any message.

## 6. Setting values from the build

- **Maven, one run:** `mvn verify -Denvironment=staging -Dwebdriver.driver=firefox`.
- **Maven, fixed in the pom:** put them in the test plugin's `<systemPropertyVariables>`. (A pom property such as `${cucumber.filter.tags}` can feed it.)
- **Gradle:** `gradle test -Denvironment=staging` does **not** reach the tests unless `build.gradle` forwards it, as for the Cucumber options (see the Cucumber page): a filtered selection inside `test { }`, such as `systemProperties System.properties.findAll { k, v -> k.toString().startsWith('serenity.') || k == 'environment' }`.

## 7. Mistakes

| Wrong | Right |
|---|---|
| `serenity.conf` in the project root | `src/test/resources/serenity.conf` |
| Reading `app.url` with `env.getProperty` when it is defined under `environments { }` | `EnvironmentSpecificConfiguration.from(env).getOptionalProperty("app.url")` |
| `-Dapp.url=...` to override a value defined under `environments { staging { } }` | `-Denvironments.staging.app.url=...` |
| An old `serenity.properties` in the project root silently beating `serenity.conf` | Keep one file, or remember that the root properties file wins |
| Setting a key through an OS environment variable | Use `-D`, the file, or the build tool's system property settings |
| Hard-coding `webdriver.chrome.driver` to a path on one machine | Leave it unset (see Troubleshooting) |

## 8. Not covered

Cloud and grid capabilities (`serenity.driver.capabilities`, `webdriver.capabilities.*`), proxies, Appium settings, the `drivers { os { } }` blocks, requirements and report customisation keys, and `.serenity` files in a user home directory. See the documentation page of Serenity properties.
