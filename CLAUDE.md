# Serenity BDD - Architecture Overview

## Project Structure

Serenity BDD is a comprehensive test automation framework that enables behavior-driven development (BDD) through expressive, maintainable test specifications. The codebase is organized as a multi-module Maven project with clear separation of concerns.

### Module Organization

```
serenity-core/
├── Core Reporting & Models
│   ├── serenity-model               # Domain model classes for test outcomes, scenarios, steps
│   ├── serenity-report-resources    # Report template resources
│   ├── serenity-reports             # Report generation and processing
│   ├── serenity-reports-configuration # Report configuration
│   ├── serenity-stats               # Test statistics processing
│   ├── serenity-json-summary-report # JSON report generation
│   └── serenity-single-page-report  # HTML single-page report generation
│
├── Core Test Execution
│   ├── serenity-core                # Main testing framework (WebDriver, Page Objects, listeners)
│   ├── serenity-screenplay          # Screenplay pattern implementation (actors, tasks, abilities)
│   │
│   └── Test Runner Integrations
│       ├── serenity-junit           # JUnit 4 integration
│       ├── serenity-junit5          # JUnit 5 integration with extensions
│       └── serenity-cucumber        # Cucumber BDD framework integration
│
├── Screenplay Extensions
│   ├── serenity-screenplay-webdriver # WebDriver abilities for Screenplay
│   ├── serenity-screenplay-rest     # REST API testing with Screenplay
│   └── serenity-screenplay-playwright # Playwright integration (experimental)
│
├── Additional Capabilities
│   ├── serenity-rest-assured        # REST API testing with RestAssured
│   ├── serenity-assertions          # Assertion library
│   ├── serenity-ensure              # Soft assertion/fluent matchers
│   ├── serenity-spring              # Spring Framework integration
│   └── serenity-appium              # Mobile testing with Appium
│
├── Cloud & Remote Execution
│   ├── serenity-saucelabs           # SauceLabs integration
│   ├── serenity-browserstack        # BrowserStack integration
│   ├── serenity-lambdatest          # LambdaTest integration
│   ├── serenity-crossbrowsertesting # CrossBrowserTesting integration
│   ├── serenity-zalenium            # Zalenium integration
│   ├── serenity-selenoid            # Selenoid integration
│   └── serenity-bitbar              # Bitbar cloud testing
│
├── Specialized Tools & Plugins
│   ├── serenity-jira-plugin         # JIRA integration
│   ├── serenity-jira-requirements-provider # JIRA requirements provider
│   ├── serenity-maven-plugin        # Maven plugin for test execution
│   ├── serenity-ant-task            # Apache Ant task
│   ├── serenity-cli                 # Command-line interface
│   ├── serenity-browsermob-plugin   # BrowserMob proxy integration
│   ├── serenity-navigator-report    # Navigator report generation
│   └── serenity-shutterbug          # Screenshot enhancement
```

## Architectural Patterns

### 1. Screenplay Pattern (Modern Approach)

The Screenplay pattern is the primary modern approach in Serenity for structuring tests. It uses an actor-centered narrative that mirrors real user behavior.

**Key Components:**

- **Actor**: Represents a user/system interacting with the application
  - Has `Abilities` - skills the actor can use (e.g., BrowseTheWeb)
  - Can `perform` Tasks and ask Questions
  - Maintains a notepad for remembering values

- **Ability**: A capability an actor possesses
  - Marker interface (`interface Ability`)
  - Examples: `BrowseTheWeb` (WebDriver), `CallAnApi` (REST)
  - Injected into actors via `actor.can(ability)`

- **Task**: High-level business operations
  - Extends `Performable`
  - Describes what the actor does in business language
  - Composed of Interactions (low-level actions)
  - Implements: `performAs(actor)` method

- **Interaction**: Low-level UI or API actions
  - Also extends `Performable`
  - Direct system interactions (click, type, navigate)
  - Atomic operations

- **Question**: Queries about the application state
  - Asked by actors: `actor.asksFor(question)`
  - Returns a value of specified type
  - Can be used in assertions via `expect(actor.asksFor(question))`

- **Stage**: Central manager for actors
  - Singleton pattern (`Stage.theStage()`)
  - Manages actor lifecycle across tests
  - Coordinates Screenplay test execution

**Example Flow:**
```
Actor "Alice" 
  → can(BrowseTheWeb.with(driver))
  → performs(OpenTheApplication.atUrl("https://..."))
  → performs(LogInWith.credentials("user", "pass"))
  → asksFor(TheCurrentPageTitle.value())
  → should(containsString("Dashboard"))
```

**Location**: `/serenity-core/serenity-screenplay/src/main/java/net/serenitybdd/screenplay/`

### 2. Page Object Pattern (Traditional Approach)

Traditional but still supported pattern for encapsulating web page interactions.

**Key Components:**

- **PageObject**: Base class for page objects
  - Wraps Selenium WebDriver functionality
  - Provides fluent API for element interactions
  - Handles waits, element location, and user actions
  - Manages page state and navigation

- **WebElementFacade**: Wrapper around WebElement
  - Enhanced element handling with waits
  - Readable error messages
  - Screenshot capture on failures
  - Smart locator resolution

- **@FindBy**: Annotation for element location
  - Similar to Selenium's @FindBy
  - Supports parameterized locators
  - Custom annotation support via `CustomFindByAnnotationService`

**Location**: `/serenity-core/serenity-core/src/main/java/net/serenitybdd/core/pages/`

### 3. Step-Based Testing

Tests can also be written using `@Step` annotated methods for reporting granularity.

**Key Components:**

- **@Step**: Annotation for test steps
  - Automatically reported in detailed reports
  - Supports parameters for data-driven tests
  - Can nest steps

- **StepEventBus**: Central event dispatcher
  - Publishes step execution events
  - Coordinates with listeners
  - Manages test session lifecycle

- **Listeners**: Event subscribers that process test events
  - `BaseStepListener` - base implementation
  - Track step execution, screenshots, failures
  - Feed data into report generation

**Location**: `/serenity-core/serenity-core/src/main/java/net/thucydides/core/steps/`

## Core Architecture Layers

### Layer 1: Test Framework Core (`serenity-core`)

**Responsibilities:**
- WebDriver management and abstraction
- Page Object support
- Step interception and instrumentation
- Screenshot capture
- Browser capability configuration
- Element location strategies
- Fluent API for interactions

**Key Classes:**
- `Serenity` - Static facade for framework access
- `WebdriverManager` - Manages WebDriver lifecycle
- `PageObject` - Base class for page objects
- `StepEventBus` - Event dispatch system
- `BaseStepListener` - Event listener base

### Layer 2: Screenplay Implementation (`serenity-screenplay`)

**Responsibilities:**
- Actor management
- Task/Interaction definition and execution
- Ability injection and management
- Question handling
- Event publishing for Screenplay actions
- Memory management for actors

**Key Classes:**
- `Actor` - Core actor representation
- `Stage` - Actor lifecycle manager
- `PerformsTasks` - Interface for task performers
- `Performable` - Base interface for actions
- `ConsequenceListener` - Monitors assertions

### Layer 3: Integration with Test Runners

**JUnit 4 Integration (`serenity-junit`):**
- `SerenityRunner` - Custom test runner
- Step factory creation
- Page object injection
- Test lifecycle management

**JUnit 5 Integration (`serenity-junit5`):**
- `SerenityJUnit5Extension` - JUnit 5 extension
- Lifecycle callbacks (BeforeEach, AfterEach)
- Parameter resolution for dependency injection
- Test execution listener implementation

**Cucumber Integration (`serenity-cucumber`):**
- Glue code for step definitions
- Scenario to test outcome mapping
- Scenario outline handling
- Background steps support

### Layer 4: Domain Model (`serenity-model`)

**Represents:**
- `TestOutcome` - Complete test result with metadata
- `ExecutedStep` - Individual step execution record
- `UserStory` / `Feature` - Requirements mapping
- `DataTable` / `ExampleTable` - Test data
- Screenshots and HTML source capture

**Location**: `serenity-model` module

### Layer 5: Reporting Engine

**Multi-format Report Generation:**
- **HTML Reports** - Interactive, feature-rich dashboards
- **JSON Reports** - Machine-readable structured output
- **Single-Page Reports** - Self-contained HTML files
- **JSON Summary** - Lightweight statistics

**Components:**
- Report generation from test outcomes
- Template processing (Freemarker)
- Asset bundling
- Statistics calculation
- Trend analysis

**Location**: `serenity-reports`, `serenity-reports-configuration`

## Key Architectural Decisions

### 1. Event-Driven Architecture

The framework uses an event bus pattern (`StepEventBus`) to decouple test execution from reporting and monitoring. This allows:
- Multiple concurrent listeners
- Non-blocking step reporting
- Flexible extension mechanisms
- Plugin architecture support

### 2. Reflection-Based Instrumentation

Tests and page objects are instrumented using:
- **ByteBuddy**: Byte code manipulation for method interception
- **Proxies**: Dynamic proxy generation for interface implementations
- **Annotations**: Declarative configuration (@Step, @FindBy, etc.)

This enables:
- Transparent step logging
- Automatic screenshot capture
- Dependency injection
- Aspect-oriented programming capabilities

### 3. Layered Dependency Injection

Uses a custom DI container (`SerenityInfrastructure`) rather than relying on external frameworks:
- `PageObjectDependencyInjector` - Injects page objects and steps
- `FieldInjectorService` - Extension points for custom injection
- Service discovery via classpath scanning

### 4. Fluent API Design

Extensive use of builder patterns and fluent interfaces for:
- Element location and interaction
- Test assertions
- Configuration
- Readable, expressive test code

**Example:**
```java
$(By.id("email")).typeAndTab("user@example.com");
waitFor(alertBox).to(appear());
expect(loginButton).to(be(enabled()));
```

### 5. Multi-Framework Support

Framework integrates with multiple test runners:
- **JUnit 4** - Traditional test framework
- **JUnit 5** - Modern parameterized and extension-based testing
- **Cucumber** - BDD scenario-based testing
- **Spock** - Groovy-based specification testing

Each integration maintains framework semantics while following runner conventions.

### 6. WebDriver Abstraction

WebDriver is abstracted through:
- `WebdriverManager` - Lifecycle management
- `DesiredCapabilities` - Driver configuration
- Remote WebDriver support
- Service pool management for parallel execution
- Multiple browser support (Chrome, Firefox, Edge, Safari, IE)

### 7. Screenshot and Source Capture

Automatic capture on:
- Step completion
- Test failure
- Explicit capture via API
- Filtered by `PhotoFilter` and `ScreenShooter` strategies

Stored with tests for evidence and debugging.

## Data Flow: Test Execution Pipeline

```
Test Method/Scenario
    ↓
Test Runner (JUnit/Cucumber)
    ↓
SerenityRunner / SerenityJUnit5Extension / CucumberStepDefinition
    ↓
StepLibraryCreator / PageObjectDependencyInjector (DI)
    ↓
Test Execution (@Step methods / Screenplay)
    ↓
StepEventBus (publishes events)
    ↓
Listeners (BaseStepListener, etc.)
    ↓
EventBusInterface (collects data)
    ↓
Photographer (captures screenshots)
    ↓
TestOutcome (aggregates results)
    ↓
Report Generators (HTML, JSON)
    ↓
Published Reports
```

## Extension Mechanisms

### Service Provider Interface (SPI)

Uses Java SPI for plugin discovery:
- `CustomFindByAnnotationProviderService` - Custom element locators
- `FieldInjectorService` - Custom dependency injection
- `ReportPortalService` - ReportPortal integration
- Cloud provider integrations (SauceLabs, BrowserStack, etc.)

### Event Publishing

Tests can listen to framework events:
- `ActorBeginsPerformanceEvent` / `ActorEndsPerformanceEvent`
- `ActorAsksQuestion`
- `TestFinishedEvent` / `TestStartedEvent`

### Custom Abilities and Tasks

Users extend the Screenplay pattern:
- Implement `Ability` interface
- Create `Task` implementations
- Add custom `Question` implementations
- Register via stage initialization

## Testing Approaches Supported

1. **Screenplay (Recommended)**
   - Actor-centric narrative
   - Task/Interaction composition
   - Best for maintainability and readability

2. **Page Objects + Steps**
   - Traditional page object pattern
   - Fluent API interactions
   - Best for existing codebases

3. **Cucumber BDD**
   - Gherkin feature files
   - Scenario-based testing
   - Best for stakeholder communication

4. **Spock Specifications**
   - Groovy-based BDD
   - Expression-oriented syntax
   - Best for specification clarity

## Configuration

**System Properties & Environment Variables:**
- `webdriver.driver` - Browser selection (chrome, firefox, edge, etc.)
- `webdriver.base.url` - Base URL for navigation
- `serenity.report.dir` - Output directory for reports
- `serenity.browser.maximized` - Browser window maximization
- `serenity.take.screenshots` - Screenshot strategy (ALWAYS, FOR_FAILURES, etc.)

**Configuration Files:**
- `serenity.properties` - Main configuration
- `serenity.env.properties` - Environment-specific settings
- `webdriver.properties` - WebDriver-specific settings

## Performance Considerations

### Parallel Execution

Serenity supports parallel test execution:
- Thread-safe `StepEventBus` with `ThreadLocal` storage
- Agent pattern for actor management
- Session isolation per thread
- `serenity-junit5` configures parallelism in pom.xml

### Screenshot Management

- Configurable screenshot strategies to reduce disk usage
- Image compression and filtering
- Screenshot digest for deduplication
- Lazy loading in reports

### Memory Efficiency

- Test outcomes serialization for distributed execution
- Event streaming (not batched)
- Cleanup hooks for resource management

## Integration Points

### CI/CD Integration

- Maven/Gradle plugin support
- Exit codes for build pipeline
- JSON reports for machine consumption
- JUnit XML format for CI systems

### Requirement Management

- JIRA requirements mapping
- Feature/Story/Scenario linking
- Requirements hierarchy tracking
- Test traceability reporting

### Cloud Platforms

- Cloud grid execution (SauceLabs, BrowserStack, etc.)
- Cross-browser testing
- Cloud provider capabilities configuration

## Key Dependencies

**Core:**
- Selenium WebDriver 4.38.0
- JUnit 4 / JUnit 5
- Cucumber 7.31.0

**Utilities:**
- Guava (collections, utilities)
- Hamcrest (matchers)
- AssertJ (fluent assertions)
- Freemarker (templates)
- SLF4J (logging)

**Build:**
- Maven 3.x
- ByteBuddy (instrumentation)
- Groovy (DSL support)

## Naming Conventions

- **Packages**: `net.serenitybdd.*` (primary), `net.thucydides.*` (legacy)
- **Tasks**: Named with business actions: `OpenTheApplication`, `SearchForProduct`
- **Questions**: Named to be asked: `TheCurrentPageTitle`, `NumberOfResults`
- **Test Classes**: `*Test`, `*Spec`, `When*` (for Gherkin-style)
- **Step Methods**: Prefixed with `Given_`, `When_`, `Then_` or use `@Step` annotation

## Summary

Serenity BDD provides a comprehensive, multi-layered testing framework that supports both modern (Screenplay) and traditional (Page Objects) testing patterns. Its architecture emphasizes readability, maintainability, and extensibility through event-driven design, reflection-based instrumentation, and rich integration with popular testing frameworks and tools. The framework excels at generating detailed, interactive reports that communicate test results to stakeholders while providing developers with the tools needed for effective test automation at scale.
