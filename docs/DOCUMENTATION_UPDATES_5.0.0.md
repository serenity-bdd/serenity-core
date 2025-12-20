# Serenity BDD Documentation Updates for 5.0.0

This document provides detailed instructions for updating the Serenity BDD documentation repository to reflect the changes in version 5.0.0.

## Overview of Changes

Version 5.0.0 introduces breaking changes related to:
1. JUnit 5 Cucumber plugin path changes (BREAKING)
2. JUnit 4 deprecation across all runners
3. JPMS compatibility improvements
4. Migration from JUnit 4 to JUnit 5

---

## 1. JUnit 5 + Cucumber Integration Documentation

### Files to Update:
- Any documentation about JUnit 5 + Cucumber integration
- Cucumber setup/getting started guides
- Example projects/code snippets

### Changes Required:

#### Old Plugin Path (4.x):
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

#### New Plugin Path (5.0.0+):
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

### Add Breaking Change Warning:

Add a prominent callout/warning box at the top of JUnit 5 + Cucumber documentation:

```markdown
> **⚠️ Breaking Change in Serenity 5.0.0**
>
> The Cucumber plugin path has changed from `io.cucumber.core.plugin.*` to `net.serenitybdd.cucumber.core.plugin.*`
>
> **Required Action**: Update the `@ConfigurationParameter` in your test suite:
> - OLD: `io.cucumber.core.plugin.SerenityReporterParallel`
> - NEW: `net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel`
>
> See the [Migration Guide](link-to-migration-guide) for details.
```

---

## 2. JUnit 4 Deprecation Warnings

### Files to Update:
- All JUnit 4 documentation pages
- Getting started guides that use JUnit 4
- Tutorial pages with JUnit 4 examples
- API reference for JUnit 4 runners

### Add Deprecation Banner:

Add this banner at the top of ALL JUnit 4 documentation pages:

```markdown
> **🚨 JUnit 4 Deprecated**
>
> JUnit 4 support is deprecated as of Serenity 5.0.0 and will be removed in Serenity 6.0.0.
>
> **Why?** JUnit 5 has been stable since 2017 and offers superior features:
> - Better parallel execution
> - Modern extension model
> - Improved IDE support
> - JPMS (Java Platform Module System) compatibility
>
> **Action Required**: Migrate to JUnit 5 before Serenity 6.0.0. See our [JUnit 4 to JUnit 5 Migration Guide](link).
>
> **Timeline**:
> - Serenity 5.x: JUnit 4 deprecated but functional
> - Serenity 6.0.0: JUnit 4 support completely removed
```

### Specific Classes to Mark as Deprecated:

When documenting these classes, add deprecation notices:

1. **SerenityRunner**
```markdown
### SerenityRunner (Deprecated)

> **⚠️ Deprecated**: `SerenityRunner` is deprecated and will be removed in Serenity 6.0.0.
> Use `SerenityJUnit5Extension` instead.

```java
// ❌ DEPRECATED - JUnit 4
@RunWith(SerenityRunner.class)
public class WhenSearchingForProducts {
    // ...
}

// ✅ RECOMMENDED - JUnit 5
@ExtendWith(SerenityJUnit5Extension.class)
class WhenSearchingForProducts {
    // ...
}
```

2. **CucumberWithSerenity / CucumberSerenityRunner**
```markdown
### CucumberWithSerenity (Deprecated)

> **⚠️ Deprecated**: JUnit 4 Cucumber runners are deprecated and will be removed in Serenity 6.0.0.
> Use JUnit 5 with `@Suite` and Cucumber JUnit Platform Engine instead.

```java
// ❌ DEPRECATED - JUnit 4
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features")
public class AcceptanceTestSuite {}

// ✅ RECOMMENDED - JUnit 5
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel"
)
public class AcceptanceTestSuite {}
```

3. **SpringIntegrationSerenityRunner**
```markdown
### SpringIntegrationSerenityRunner (Deprecated)

> **⚠️ Deprecated**: `SpringIntegrationSerenityRunner` is part of the deprecated JUnit 4 support.
> Currently, there is no direct JUnit 5 replacement. Use Spring's native `@SpringJUnitConfig`
> with `@ExtendWith(SerenityJUnit5Extension.class)` as a workaround.

A dedicated `serenity-spring-junit5` module is planned for a future release.
```

---

## 3. Create New Migration Guide Page

### Create New File: `migration-guide-5.0.0.md`

This should be a prominent, easy-to-find page. Suggested location:
- `/docs/migration/` or `/docs/guides/migration/`

### Content Structure:

```markdown
# Migrating to Serenity BDD 5.0.0

Serenity BDD 5.0.0 is a major release with important breaking changes and deprecations. This guide will help you upgrade smoothly.

## Breaking Changes

### 1. JUnit 5 + Cucumber: Plugin Path Changed

**Who is affected**: All users running Cucumber tests with JUnit 5

**What changed**: The Cucumber plugin class package changed from `io.cucumber.core.plugin` to `net.serenitybdd.cucumber.core.plugin`

**Required action**: Update your test suite configuration

#### Before (4.x):
```java
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "io.cucumber.core.plugin.SerenityReporterParallel"
)
```

#### After (5.0.0):
```java
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel"
)
```

**Why**: To resolve JPMS (Java Platform Module System) split package violations and prepare for Cucumber's migration to Java 17+ modules.

---

## Deprecations

### 2. JUnit 4 Support Deprecated

**Who is affected**: All users using JUnit 4 with Serenity

**What changed**: All JUnit 4 runners are now deprecated and marked for removal

**Timeline**:
- **Serenity 5.x**: JUnit 4 deprecated but functional (you'll see warnings)
- **Serenity 6.0.0**: JUnit 4 support completely removed

**Deprecated classes**:
- `SerenityRunner`
- `CucumberSerenityRunner`
- `CucumberSerenityBaseRunner`
- `CucumberWithSerenity`
- `SpringIntegrationSerenityRunner`

**Required action**: Plan migration to JUnit 5. See [JUnit 4 to JUnit 5 Migration](#migrating-from-junit-4-to-junit-5) below.

---

## Migration Guides

### Migrating JUnit 5 Cucumber Tests (Breaking Change)

#### Step 1: Update Maven/Gradle Dependencies

Ensure you're using Serenity 5.0.0:

**Maven:**
```xml
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-cucumber</artifactId>
    <version>5.0.0</version>
    <scope>test</scope>
</dependency>
```

**Gradle:**
```groovy
testImplementation 'net.serenity-bdd:serenity-cucumber:5.0.0'
```

#### Step 2: Update Test Suite Configuration

Find all test suite classes and update the plugin path:

**Find and Replace:**
- `io.cucumber.core.plugin.SerenityReporter` → `net.serenitybdd.cucumber.core.plugin.SerenityReporter`
- `io.cucumber.core.plugin.SerenityReporterParallel` → `net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel`

**Example:**
```java
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel"  // ✅ Updated
)
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "com.example.steps"
)
public class CucumberTestSuite {}
```

#### Step 3: Verify Tests Run

```bash
mvn clean verify
```

That's it! No other changes needed.

---

### Migrating from JUnit 4 to JUnit 5

**Note**: This migration is optional in Serenity 5.x but will be required in 6.0.0.

#### For Regular Serenity Tests (Non-Cucumber)

##### Before (JUnit 4):
```java
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class WhenSearchingForProducts {

    @Test
    public void shouldFindProduct() {
        // test code
    }
}
```

##### After (JUnit 5):
```java
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SerenityJUnit5Extension.class)
class WhenSearchingForProducts {

    @Test
    void shouldFindProduct() {
        // test code
    }
}
```

**Key Changes**:
1. Replace `@RunWith(SerenityRunner.class)` → `@ExtendWith(SerenityJUnit5Extension.class)`
2. Replace `@Test` from `org.junit.Test` → `org.junit.jupiter.api.Test`
3. Replace `@Before`/`@After` → `@BeforeEach`/`@AfterEach`
4. Replace `@BeforeClass`/`@AfterClass` → `@BeforeAll`/`@AfterAll` (must be static)
5. Class can be package-private (no need for `public`)
6. Test methods can be package-private (no need for `public`)

#### For Cucumber Tests

##### Before (JUnit 4):
```java
import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.example.steps",
    tags = "@smoke"
)
public class CucumberTestSuite {}
```

##### After (JUnit 5):
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
    value = "com.example.steps"
)
@ConfigurationParameter(
    key = FILTER_TAGS_PROPERTY_NAME,
    value = "@smoke"
)
class CucumberTestSuite {}
```

**Key Changes**:
1. Replace `@RunWith(CucumberWithSerenity.class)` → `@Suite` + `@IncludeEngines("cucumber")`
2. Replace `@CucumberOptions` → Multiple `@ConfigurationParameter` annotations
3. Add Serenity reporter plugin configuration
4. Import from `org.junit.platform.suite.api.*`

#### Configuration Parameter Mapping

| JUnit 4 @CucumberOptions       | JUnit 5 @ConfigurationParameter                  |
|--------------------------------|--------------------------------------------------|
| `features = "path"`            | `@SelectClasspathResource("path")`               |
| `glue = "package"`             | `key = GLUE_PROPERTY_NAME, value = "package"`    |
| `tags = "@tag"`                | `key = FILTER_TAGS_PROPERTY_NAME, value = "@tag"`|
| `plugin = "pretty"`            | `key = PLUGIN_PROPERTY_NAME, value = "pretty"`   |
| `dryRun = true`                | `key = "cucumber.execution.dry-run", value = "true"` |
| `snippets = CAMELCASE`         | `key = "cucumber.snippet-type", value = "camelcase"` |

#### Update Dependencies

**Maven - Remove JUnit 4:**
```xml
<!-- Remove -->
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
    <version>7.33.0</version>
    <scope>test</scope>
</dependency>
```

#### Parallel Execution (JUnit 5 Bonus)

JUnit 5 has excellent parallel execution support:

```java
@ConfigurationParameter(
    key = "cucumber.execution.parallel.enabled",
    value = "true"
)
@ConfigurationParameter(
    key = "cucumber.execution.parallel.config.strategy",
    value = "dynamic"
)
```

Or in `junit-platform.properties`:
```properties
cucumber.execution.parallel.enabled=true
cucumber.execution.parallel.config.strategy=dynamic
cucumber.execution.parallel.config.fixed.parallelism=4
```

---

## What If You Can't Migrate Yet?

If you need to stay on JUnit 4 for now:

1. **Use Serenity 5.x**: JUnit 4 still works but you'll see deprecation warnings
2. **Suppress warnings** (not recommended):
   ```java
   @SuppressWarnings("deprecation")
   @RunWith(SerenityRunner.class)
   public class MyTest {}
   ```
3. **Plan migration**: You have until Serenity 6.0.0 (likely 6-12 months)

---

## Other Changes in 5.0.0

### Bug Fixes
- Fixed screenshot viewer always opening at index 0 (#3685)
- Fixed report sorting by test outcome (#3700)

### Dependency Updates
- Selenium 4.39.0
- Cucumber 7.33.0
- RestAssured 6.0.0
- Appium 10.0.0
- JUnit 5: 6.0.1
- Groovy 4.0.29 (JDK 25 support)

### JPMS Compatibility
- Resolved split package violations with Cucumber
- Moved 20+ classes from `io.cucumber.*` to `net.serenitybdd.cucumber.*` packages
- Created Serenity-owned `TestSourcesModel` using public Cucumber APIs

---

## Need Help?

- **Migration Issues**: [GitHub Discussions](https://github.com/serenity-bdd/serenity-core/discussions)
- **Bug Reports**: [GitHub Issues](https://github.com/serenity-bdd/serenity-core/issues)
- **Full Release Notes**: [5.0.0 Release Notes](https://github.com/serenity-bdd/serenity-core/blob/main/docs/release-notes/5.0.0.md)
- **Commercial Support**: [Serenity Dojo](https://www.serenitydojo.academy/serenity-bdd-support-packages)

---

## FAQ

**Q: Do I need to update if I'm not using Cucumber?**
A: If you're using JUnit 5 without Cucumber, no breaking changes affect you. If you're using JUnit 4, you'll see deprecation warnings but tests will work.

**Q: Can I use both JUnit 4 and JUnit 5 during migration?**
A: Yes, you can have both in the same project during the transition period.

**Q: What about SpringIntegrationSerenityRunner?**
A: It's deprecated along with other JUnit 4 runners. A JUnit 5 replacement (`serenity-spring-junit5`) is planned for a future release. For now, use Spring's `@SpringJUnitConfig` with `@ExtendWith(SerenityJUnit5Extension.class)`.

**Q: Will my JUnit 4 tests still run?**
A: Yes, in Serenity 5.x JUnit 4 tests will run normally, but you'll see deprecation warnings.

**Q: When exactly will JUnit 4 support be removed?**
A: In Serenity 6.0.0, likely 6-12 months after 5.0.0 release.

**Q: Why is JUnit 4 being deprecated?**
A: JUnit 5 has been stable since 2017 and offers superior features. More importantly, Cucumber is moving to Java 17+ with JPMS modules, which will break the current JUnit 4 integration due to split package violations.
```

---

## 4. Update Getting Started / Quick Start Guides

### For ALL Getting Started Pages:

1. **Default to JUnit 5 examples** (not JUnit 4)
2. **Add version note** at the top:
   ```markdown
   > This guide uses JUnit 5, which is the recommended approach for Serenity BDD 5.0.0+.
   > If you're using JUnit 4, see the [JUnit 4 to JUnit 5 Migration Guide](link).
   ```

3. **Update Maven/Gradle dependencies** to use 5.0.0:
   ```xml
   <serenity.version>5.0.0</serenity.version>
   ```

4. **Update Cucumber plugin paths** in all examples

---

## 5. Update API Documentation / Reference

### Deprecated Classes Section:

Add a new section listing all deprecated classes with alternatives:

```markdown
## Deprecated Classes (5.0.0)

The following classes are deprecated and will be removed in Serenity 6.0.0:

### JUnit 4 Runners

| Deprecated Class | Replacement | Notes |
|-----------------|-------------|-------|
| `SerenityRunner` | `SerenityJUnit5Extension` | Core JUnit 4 test runner |
| `CucumberWithSerenity` | JUnit 5 `@Suite` + Cucumber Platform Engine | Cucumber + JUnit 4 runner |
| `CucumberSerenityRunner` | JUnit 5 `@Suite` + Cucumber Platform Engine | Cucumber + JUnit 4 base runner |
| `SpringIntegrationSerenityRunner` | TBD (planned for 5.x/6.0) | Spring + JUnit 4 integration |

See the [Migration Guide](link) for detailed instructions.
```

---

## 6. Update Example Projects

If the documentation includes example projects or code repositories:

### Update All Examples:
1. Change version to `5.0.0`
2. Update JUnit 5 + Cucumber plugin paths
3. Add migration examples showing both JUnit 4 (deprecated) and JUnit 5 side-by-side
4. Add README notes about deprecations

---

## 7. Update Search/Index Keywords

Make sure these terms are searchable:
- "JUnit 5 migration"
- "JUnit 4 deprecated"
- "Cucumber plugin path"
- "5.0.0 breaking changes"
- "JPMS compatibility"
- "SerenityReporterParallel"

---

## 8. Add Upgrade Checklist

Create a quick reference checklist page:

```markdown
# Serenity 5.0.0 Upgrade Checklist

## For JUnit 5 + Cucumber Users (REQUIRED)

- [ ] Update Serenity version to 5.0.0 in `pom.xml`/`build.gradle`
- [ ] Find all test suite classes
- [ ] Replace `io.cucumber.core.plugin.SerenityReporter` → `net.serenitybdd.cucumber.core.plugin.SerenityReporter`
- [ ] Replace `io.cucumber.core.plugin.SerenityReporterParallel` → `net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel`
- [ ] Run tests: `mvn clean verify`
- [ ] Verify reports generate correctly

## For JUnit 4 Users (RECOMMENDED)

- [ ] Note deprecation warnings in build output
- [ ] Review [JUnit 4 to JUnit 5 Migration Guide](link)
- [ ] Plan migration timeline (required before 6.0.0)
- [ ] Update dependencies to include JUnit 5
- [ ] Convert test classes one by one
- [ ] Update CI/CD pipelines if needed

## For Non-Cucumber Users

- [ ] Update Serenity version to 5.0.0
- [ ] Run tests to ensure compatibility
- [ ] Consider migrating from JUnit 4 to JUnit 5 (if applicable)
```

---

## 9. Update Version Compatibility Matrix

Update any version compatibility tables:

```markdown
| Serenity Version | JUnit 4 | JUnit 5 | Cucumber | Selenium | Java |
|-----------------|---------|---------|----------|----------|------|
| 5.0.0           | ⚠️ Deprecated | ✅ Recommended | 7.33.0 | 4.39.0 | 11+ |
| 4.3.4           | ✅ Supported | ✅ Supported | 7.31.0 | 4.38.0 | 11+ |
```

---

## 10. Homepage / Landing Page Updates

Add a prominent banner or announcement:

```markdown
> **📢 Serenity BDD 5.0.0 Released**
>
> Major release with breaking changes for JUnit 5 + Cucumber users.
> [Read the Migration Guide →](link)
```

---

## Summary of Documentation Changes

### Critical Updates (Breaking Changes):
1. ✅ Update ALL JUnit 5 + Cucumber examples with new plugin path
2. ✅ Add breaking change warnings to affected pages
3. ✅ Create comprehensive migration guide

### Important Updates (Deprecations):
4. ✅ Add deprecation banners to all JUnit 4 documentation
5. ✅ Create JUnit 4 to JUnit 5 migration section
6. ✅ Update getting started guides to default to JUnit 5

### Supporting Updates:
7. ✅ Update version numbers throughout documentation
8. ✅ Update API reference with deprecated classes
9. ✅ Update example projects
10. ✅ Add upgrade checklist

---

## Testing the Documentation

After making changes, verify:
- [ ] All code examples are syntactically correct
- [ ] All links work (no broken links)
- [ ] Search finds new migration content
- [ ] Breaking change warnings are visible
- [ ] JUnit 4 deprecation is clear throughout

---

## Files Likely to Need Updates

(Adjust based on actual documentation structure)

```
/docs/
  ├── getting-started/
  │   ├── junit5.md                    # ✅ Update plugin path
  │   ├── cucumber.md                  # ✅ Update plugin path
  │   └── junit4.md                    # ✅ Add deprecation banner
  ├── guides/
  │   ├── cucumber-junit5.md           # ✅ Update plugin path
  │   ├── junit4-migration.md          # ✅ NEW FILE
  │   └── parallel-execution.md        # ✅ Update examples
  ├── migration/
  │   └── 5.0.0.md                     # ✅ NEW FILE
  ├── reference/
  │   ├── junit-runners.md             # ✅ Add deprecations
  │   └── cucumber-integration.md      # ✅ Update plugin path
  └── tutorials/
      └── *.md                         # ✅ Update all examples
```

---

## Commit Message Template

When committing documentation updates:

```
docs: Update documentation for Serenity 5.0.0 release

Updated documentation to reflect breaking changes and deprecations in 5.0.0:
- Updated all JUnit 5 + Cucumber examples with new plugin path
- Added deprecation warnings to JUnit 4 documentation
- Created comprehensive migration guide
- Updated getting started guides to default to JUnit 5
- Added upgrade checklist and FAQ

Breaking change: Cucumber plugin path moved from io.cucumber.core.plugin
to net.serenitybdd.cucumber.core.plugin
```

---

## Contact for Questions

If you need clarification on any of these changes, the following resources from the serenity-core repository may help:

- [5.0.0 Release Notes](https://github.com/serenity-bdd/serenity-core/blob/main/docs/release-notes/5.0.0.md)
- [Migration Guide](https://github.com/serenity-bdd/serenity-core/blob/main/serenity-cucumber/MIGRATION_5.0.0.md)
- [Cucumber API Requirements](https://github.com/serenity-bdd/serenity-core/blob/main/serenity-cucumber/CUCUMBER_API_REQUIREMENTS.md)
