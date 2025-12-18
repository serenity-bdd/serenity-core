# Serenity BDD TestSourcesModel Dependency Analysis

## Executive Summary

Serenity BDD currently depends on `io.cucumber.core.plugin.TestSourcesModel`, a package-private class in Cucumber's core plugin package. We understand this class is planned for removal in an upcoming Cucumber release, which will break Serenity's integration completely.

This document outlines:
- The specific methods Serenity uses from TestSourcesModel
- Why Serenity needs access to these capabilities
- The gap in Cucumber's current public API
- Proposed solutions to maintain compatibility

## Impact Assessment

**Severity**: Showstopping - will break Serenity integration entirely

**Affected Serenity Classes**: 5 core plugin classes that handle feature file parsing and test reporting

## Current Dependencies

### TestSourcesModel Methods Used by Serenity

Serenity uses the following methods from `io.cucumber.core.plugin.TestSourcesModel`:

1. **`void addTestSourceReadEvent(URI uri, TestSourceRead event)`**
   - Purpose: Store parsed feature file data
   - Used by: All 5 affected classes during feature file loading

2. **`Feature getFeature(URI uri)`**
   - Returns: `io.cucumber.messages.types.Feature`
   - Purpose: Retrieve the parsed Feature object for a given feature file
   - Used by: `FeatureFileLoader`, reporter classes

3. **`AstNode getAstNode(URI uri, int line)`**
   - Returns: `TestSourcesModel.AstNode` (wrapper around parsed AST)
   - Purpose: Locate the AST node at a specific line number in a feature file
   - Used by: All reporter classes to correlate test events with Gherkin elements

4. **`static Scenario getScenarioDefinition(AstNode astNode)`**
   - Returns: `io.cucumber.messages.types.Scenario`
   - Purpose: Extract the Scenario object from an AST node
   - Used by: Context and reporter classes to access scenario metadata

5. **`static Optional<Background> getBackgroundForTestCase(AstNode astNode)`**
   - Returns: `Optional<io.cucumber.messages.types.Background>`
   - Purpose: Find the Background section associated with a scenario
   - Used by: Reporter classes to include background steps in reports

6. **`static String convertToId(String name)`**
   - Returns: Normalized ID string
   - Purpose: Convert scenario names to standardized IDs
   - Used by: Reporter classes for scenario identification
   - **Note**: This method has been successfully replicated using simple regex (pattern: `[\s'_,!]`)

### Affected Serenity Classes

The following Serenity classes directly use TestSourcesModel:

1. **`io.cucumber.core.plugin.FeatureFileLoader`**
   - Uses: `getFeature(URI)`, `getAstNode(URI, int)`
   - Purpose: Load and parse feature files for test execution
   - Lines: 36, 127

2. **`io.cucumber.core.plugin.ScenarioContext`**
   - Uses: `getScenarioDefinition(AstNode)`
   - Purpose: Maintain context about the currently executing scenario
   - Line: 94

3. **`io.cucumber.core.plugin.ScenarioContextParallel`**
   - Uses: `getScenarioDefinition(AstNode)`
   - Purpose: Thread-safe scenario context for parallel execution
   - Line: 97

4. **`io.cucumber.core.plugin.SerenityReporter`**
   - Uses: `getScenarioDefinition(AstNode)`, `getBackgroundForTestCase(AstNode)`, `convertToId(String)`
   - Purpose: Generate detailed test reports with scenario metadata
   - Lines: 178, 312, 445

5. **`io.cucumber.core.plugin.SerenityReporterParallel`**
   - Uses: `getScenarioDefinition(AstNode)`, `getBackgroundForTestCase(AstNode)`, `convertToId(String)`
   - Purpose: Thread-safe reporter for parallel test execution
   - Lines: 185, 298, 421

## Why Serenity Needs This Data

Serenity BDD generates comprehensive, living documentation reports from test execution. To do this, we need access to:

### From `io.cucumber.messages.types.Feature`:
- `getName()` - Feature title for report headers
- `getDescription()` - Feature description for documentation
- `getTags()` - Feature-level tags for filtering and categorization
- `getChildren()` - Scenarios and Rules within the feature
- `getLanguage()` - I18n support

### From `io.cucumber.messages.types.Scenario`:
- `getName()` - Scenario title
- `getDescription()` - Scenario description for documentation
- `getTags()` - Scenario tags for requirements mapping
- `getSteps()` - Step definitions for detailed reporting
- `getExamples()` - Example tables for scenario outlines
- `getLocation()` - Line numbers for traceability

### From `io.cucumber.messages.types.Background`:
- `getName()` - Background section title
- `getSteps()` - Background steps to include in reports
- `getLocation()` - Line numbers for traceability

### Use Case: Line Number to AST Correlation

During test execution, Cucumber fires events with line numbers. Serenity needs to:
1. Receive an event with `(URI, line number)`
2. Look up the AST node at that location
3. Extract the Scenario/Background/Feature object
4. Access metadata (name, description, tags, steps)
5. Include this in detailed reports

**Example Flow**:
```java
// Cucumber fires event
TestCaseStarted event = ... // contains URI and line number

// Serenity needs to do:
AstNode node = testSourcesModel.getAstNode(event.getTestCase().getUri(), event.getTestCase().getLine());
Scenario scenario = TestSourcesModel.getScenarioDefinition(node);

// Then access:
String scenarioName = scenario.getName();
String description = scenario.getDescription();
List<Tag> tags = scenario.getTags();
List<Step> steps = scenario.getSteps();
```

## Gap in Current Public API

### What We've Tried

We attempted to replicate TestSourcesModel functionality using Cucumber's public APIs:

1. **`io.cucumber.plugin.event.TestSourceParsed`** - Public event that provides parsed AST
   - Contains: `Collection<Node>` - Cucumber's public Node API
   - Limitation: Node provides structure but not the rich data objects

2. **`io.cucumber.plugin.event.Node`** - Public AST node interface
   - Provides: `getLocation()`, `getName()`, `getKeyword()`, `getParent()`, `elements()`
   - **Missing**: No way to access underlying `messages.types.Scenario/Background/Feature` objects

3. **`io.cucumber.core.feature.FeatureParser`** - Public parser
   - Can parse feature files into `Optional<Feature>`
   - Limitation: Doesn't provide line-to-node indexing

### The Core Problem

**Cucumber's public Node API does not expose the underlying `io.cucumber.messages.types.*` objects.**

The Node interface provides AST structure and navigation, but Serenity needs access to the rich metadata contained in:
- `io.cucumber.messages.types.Feature`
- `io.cucumber.messages.types.Scenario`
- `io.cucumber.messages.types.Background`
- `io.cucumber.messages.types.Step`
- `io.cucumber.messages.types.Tag`

### Code Example Showing the Gap

```java
// We can get the Node from TestSourceParsed event
public void handleTestSourceParsed(TestSourceParsed event) {
    Collection<Node> nodes = event.getNodes();

    for (Node node : nodes) {
        if (node instanceof Node.Scenario) {
            Node.Scenario scenarioNode = (Node.Scenario) node;

            // We can access:
            Optional<String> name = scenarioNode.getName();  // ✓ Available
            int line = scenarioNode.getLocation().getLine(); // ✓ Available

            // We CANNOT access:
            // - scenario.getDescription()  ✗ Not available
            // - scenario.getTags()         ✗ Not available
            // - scenario.getSteps()        ✗ Not available
            // - scenario.getExamples()     ✗ Not available

            // The Node doesn't expose the underlying messages.types.Scenario
        }
    }
}
```

## Proposed Solutions

We see three possible approaches to resolve this issue:

### Option 1: Make TestSourcesModel Public (Minimal Change)

**Approach**: Change `TestSourcesModel` from package-private to public

**Pros**:
- Minimal code change
- Maintains existing API
- No breaking changes for current users
- Works immediately

**Cons**:
- Exposes internal implementation
- Increases maintenance burden

### Option 2: Enhance Node API (Recommended)

**Approach**: Add methods to the Node interfaces to expose messages.types objects

**Proposed API additions**:
```java
public interface Node {
    // Existing methods...

    /**
     * Get the underlying messages.types object for this node.
     * @return Optional containing the messages type object
     */
    Optional<Object> getMessagesType();

    interface Scenario extends Node {
        /**
         * Get the full Scenario object from messages.types.
         * @return The Scenario with all metadata
         */
        io.cucumber.messages.types.Scenario getScenario();
    }

    interface Feature extends Node {
        /**
         * Get the full Feature object from messages.types.
         * @return The Feature with all metadata
         */
        io.cucumber.messages.types.Feature getFeature();
    }
}
```

**Pros**:
- Extends existing public API naturally
- Maintains clean architecture
- Provides full access to metadata
- Future-proof for other integrations

**Cons**:
- Requires API design and implementation
- Larger code change than Option 1

### Option 3: Provide AstNodeIndex Public API (Alternative)

**Approach**: Create a new public API specifically for line-to-AST lookups

**Proposed API**:
```java
package io.cucumber.plugin.event;

/**
 * Public API for looking up AST elements by line number.
 */
public interface GherkinDocumentIndex {
    /**
     * Get the Scenario at a specific line in a feature file.
     */
    Optional<io.cucumber.messages.types.Scenario> getScenarioAt(URI uri, int line);

    /**
     * Get the Feature for a feature file.
     */
    Optional<io.cucumber.messages.types.Feature> getFeature(URI uri);

    /**
     * Get the Background associated with a scenario.
     */
    Optional<io.cucumber.messages.types.Background> getBackgroundFor(URI uri, int scenarioLine);
}
```

**Pros**:
- Purpose-built for this use case
- Clean separation of concerns
- Doesn't expose implementation details

**Cons**:
- New API to design and maintain
- Duplicates some TestSourcesModel functionality

## Serenity's Commitment

We are committed to:
- Using only public, stable Cucumber APIs
- Avoiding split packages (JPMS compliance)
- Following Cucumber's architectural direction
- Providing feedback on API design if requested

We have already migrated 12 classes out of `io.cucumber` packages to `net.serenitybdd.cucumber` packages as part of our JPMS compliance work. The TestSourcesModel dependency is the last critical blocker.

## Request for Feedback

We would greatly appreciate the Cucumber team's input on:

1. Which solution approach aligns best with Cucumber's architecture?
2. Is there an existing public API we've overlooked that could solve this?
3. What timeline can we expect for TestSourcesModel removal?
4. Can we collaborate on API design if Option 2 or 3 is chosen?

