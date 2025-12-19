# Serenity BDD 5.0.0 Migration Guide

## Breaking Changes

### 1. JUnit 5 Cucumber Plugin Path Changed

The Cucumber plugin class has been moved from `io.cucumber.core.plugin` to `net.serenitybdd.cucumber.core.plugin` to resolve JPMS split package violations.

**Required Action:** Update your JUnit 5 test suite configuration.

#### Before (4.x):
```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "io.cucumber.core.plugin.SerenityReporterParallel"
)
public class CucumberTestSuite {}
```

#### After (5.0.0):
```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel"
)
public class CucumberTestSuite {}
```

**Find and Replace:**
- Replace: `io.cucumber.core.plugin.SerenityReporter` → `net.serenitybdd.cucumber.core.plugin.SerenityReporter`
- Replace: `io.cucumber.core.plugin.SerenityReporterParallel` → `net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel`

### 2. JUnit 4 Cucumber Support Officially Deprecated

JUnit 4 Cucumber integration is now officially deprecated and will be **removed in Serenity 6.0.0**.

**Why:**
- JUnit 5 was released in September 2017 (8 years ago)
- Cucumber is moving to Java 17+ with JPMS modules
- JPMS does not allow split packages, which the JUnit 4 integration requires
- When Cucumber migrates to JPMS, JUnit 4 integration will stop working entirely

**Timeline:**
- **Serenity 5.0.0** (current): JUnit 4 support deprecated but still functional
- **Serenity 6.0.0** (future): JUnit 4 support completely removed

**Affected Classes:**
- `io.cucumber.junit.CucumberSerenityRunner` - deprecated, forRemoval=true
- `io.cucumber.junit.CucumberSerenityBaseRunner` - deprecated, forRemoval=true
- `net.serenitybdd.cucumber.CucumberWithSerenity` - deprecated, forRemoval=true

## Migration from JUnit 4 to JUnit 5

### Step 1: Update Dependencies

Update your `pom.xml`:

```xml
<!-- Remove JUnit 4 -->
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <scope>test</scope>
</dependency>

<!-- Add JUnit 5 Platform -->
<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-suite</artifactId>
    <scope>test</scope>
</dependency>

<!-- Add Cucumber JUnit Platform Engine -->
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit-platform-engine</artifactId>
    <version>${cucumber.version}</version>
    <scope>test</scope>
</dependency>
```

### Step 2: Convert Test Suite Class

#### JUnit 4 (Old):
```java
import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.example.stepdefinitions",
    tags = "@smoke"
)
public class CucumberTestSuite {}
```

#### JUnit 5 (New):
```java
import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel"
)
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "com.example.stepdefinitions"
)
@ConfigurationParameter(
    key = FILTER_TAGS_PROPERTY_NAME,
    value = "@smoke"
)
public class CucumberTestSuite {}
```

### Step 3: Update Maven Surefire Plugin

Configure Surefire to use JUnit 5:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0-M5</version>
    <configuration>
        <includes>
            <include>**/CucumberTestSuite.java</include>
        </includes>
    </configuration>
</plugin>
```

### Step 4: Parallel Execution (Optional)

JUnit 5 has excellent parallel execution support:

```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel"
)
@ConfigurationParameter(
    key = "cucumber.execution.parallel.enabled",
    value = "true"
)
@ConfigurationParameter(
    key = "cucumber.execution.parallel.config.strategy",
    value = "dynamic"
)
public class CucumberTestSuite {}
```

Or configure via `junit-platform.properties`:

```properties
cucumber.execution.parallel.enabled=true
cucumber.execution.parallel.config.strategy=dynamic
cucumber.execution.parallel.config.fixed.parallelism=4
```

## Configuration Parameter Mapping

| JUnit 4 @CucumberOptions       | JUnit 5 @ConfigurationParameter                  |
|--------------------------------|--------------------------------------------------|
| `features = "path"`            | `@SelectClasspathResource("path")`               |
| `glue = "package"`             | `key = GLUE_PROPERTY_NAME, value = "package"`    |
| `tags = "@tag"`                | `key = FILTER_TAGS_PROPERTY_NAME, value = "@tag"`|
| `plugin = "pretty"`            | `key = PLUGIN_PROPERTY_NAME, value = "pretty"`   |
| `dryRun = true`                | `key = "cucumber.execution.dry-run", value = "true"` |
| `snippets = CAMELCASE`         | `key = "cucumber.snippet-type", value = "camelcase"` |

## Benefits of JUnit 5

1. **Better Parallel Execution**: Built-in support for parallel test execution
2. **More Flexible**: Better extension model and configuration options
3. **Modern**: Active development and new features
4. **JPMS Compatible**: Works with Java modules
5. **Better IDE Support**: Improved test discovery and execution in modern IDEs

## Common Issues

### Issue: Tests Not Discovered

**Solution:** Ensure your test class is named correctly and the JUnit Platform Suite dependency is included.

### Issue: Serenity Reports Not Generated

**Solution:** Verify the plugin configuration parameter uses the correct new path:
```java
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel"
)
```

### Issue: Step Definitions Not Found

**Solution:** Check the GLUE_PROPERTY_NAME configuration points to your step definitions package:
```java
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "com.example.stepdefinitions"
)
```

## Additional Resources

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Cucumber JUnit Platform Engine](https://github.com/cucumber/cucumber-jvm/tree/main/cucumber-junit-platform-engine)
- [Serenity BDD Documentation](https://serenity-bdd.github.io/)

## Support

If you encounter issues during migration:
1. Check the [Serenity BDD GitHub Issues](https://github.com/serenity-bdd/serenity-core/issues)
2. Ask questions on the [Serenity BDD Discussions](https://github.com/serenity-bdd/serenity-core/discussions)
3. Review the example projects in [serenity-demos](https://github.com/serenity-bdd/serenity-demos)