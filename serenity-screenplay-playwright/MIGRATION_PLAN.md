# Serenity Screenplay Playwright Migration Plan

## Executive Summary

This plan outlines the migration of `serenity-screenplay-playwright` to properly integrate with the `serenity-playwright` module and align with the patterns established in `serenity-screenplay-webdriver`. The approach is **outside-in test-first**: for each feature, write a sample Screenplay test first, then implement the code to make it pass.

---

## Current State Analysis

### What Exists

**serenity-playwright module (Step Library Integration)**
- Playwright version: **1.57.0**
- `PlaywrightSerenity` - Main API for page registration
- `PlaywrightPageRegistry` - ThreadLocal page registry
- `PlaywrightStepListener` - Automatic screenshot capture at step boundaries
- `PlaywrightPhotoLens` - Screenshot utility
- `SerenityPlaywrightExtension` - JUnit 5 extension for auto-registration
- `@SerenityPlaywright` - Meta-annotation for JUnit 5

**serenity-screenplay-playwright module (Current Screenplay Integration)**
- Playwright version: **1.52.0** (outdated)
- `BrowseTheWebWithPlaywright` ability (functional but missing `HasTeardown`)
- 11 interaction classes (Click, Enter, Open, etc.)
- 4 question classes (Text, Visibility, Attribute, SelectOptions)
- Simple `Target` class (limited compared to WebDriver version)
- `Ensure` assertions (basic)
- 7 test files against live website

### Key Gaps Identified

| Gap | Current State | Target State |
|-----|---------------|--------------|
| Playwright version | 1.52.0 | 1.57.0 (match serenity-playwright) |
| Module dependency | Standalone | Depends on serenity-playwright |
| JUnit 5 support | None | Full JUnit 5 extension integration |
| `@CastMember` injection | Not implemented | PlaywrightCapableActorInjector |
| Target abstraction | Simple CSS/XPath | Full hierarchy (iFrame, chaining, etc.) |
| `HasTeardown` | Not implemented | Proper cleanup via wrapUp() |
| Step listener | Custom screenshot logic | Leverage PlaywrightStepListener |
| Interactions | 11 classes | ~40 core interactions |
| Questions | 4 classes | ~20 core questions |
| Locator strategies | Basic | Text, role, aria, chaining |
| Frame support | None | iFrame traversal |

---

## Migration Phases

### Phase 1: Foundation & Module Integration

**Goal:** Establish proper module dependency and version alignment.

#### 1.1 Update Dependencies

**Test first:** Write a test that imports both `serenity-playwright` and `serenity-screenplay-playwright` classes.

```java
// src/test/java/net/serenitybdd/screenplay/playwright/integration/ModuleIntegrationTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class ModuleIntegrationTest {

    @Test
    void should_be_able_to_use_both_screenplay_and_step_library_playwright() {
        // This test validates module compatibility
        Actor alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

        alice.attemptsTo(
            Open.url("https://example.com")
        );

        // Verify PlaywrightSerenity can see the registered page
        assertThat(PlaywrightSerenity.getRegisteredPages()).isNotEmpty();
    }
}
```

**Implementation:**
1. Update `pom.xml` to:
   - Set Playwright version to 1.57.0
   - Add dependency on `serenity-playwright`
   - Add dependency on `serenity-junit5`

2. Modify `BrowseTheWebWithPlaywright`:
   - Register page with `PlaywrightSerenity.registerPage(page)` when creating page
   - Unregister on cleanup

#### 1.2 Implement HasTeardown

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/abilities/TeardownTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class TeardownTest {

    @Test
    void ability_should_cleanup_resources_on_wrapUp() {
        Actor alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

        alice.attemptsTo(Open.url("https://example.com"));
        Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();

        // Trigger cleanup
        alice.wrapUp();

        // Page should be closed
        assertThat(page.isClosed()).isTrue();
    }
}
```

**Implementation:**
- Add `implements HasTeardown` to `BrowseTheWebWithPlaywright`
- Implement `tearDown()` method to close browser/context/page

---

### Phase 2: JUnit 5 Integration

**Goal:** Full JUnit 5 support with `@CastMember` annotation.

#### 2.1 Create PlaywrightCapableActorInjector

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/injectors/CastMemberInjectionTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class CastMemberInjectionTest {

    @CastMember(name = "Alice")
    Actor alice;

    @Test
    void should_inject_actor_with_playwright_ability() {
        assertThat(alice).isNotNull();
        assertThat(alice.abilityTo(BrowseTheWebWithPlaywright.class)).isNotNull();

        alice.attemptsTo(
            Open.url("https://example.com"),
            Ensure.that("h1").isVisible()
        );
    }
}
```

**Implementation:**
1. Create `PlaywrightCapableActorInjector` (similar to `WebCapableActorInjector`)
2. Register via SPI in `META-INF/services/net.serenitybdd.model.di.DependencyInjector`

#### 2.2 Create @PlaywrightCastMember Annotation

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/AnnotationTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class AnnotationTest {

    @PlaywrightCastMember(name = "Bob", browserType = "firefox")
    Actor bob;

    @Test
    void should_support_custom_browser_type() {
        bob.attemptsTo(Open.url("https://example.com"));
        // Verify Firefox is used (via user agent or other means)
    }
}
```

**Implementation:**
- Create `@PlaywrightCastMember` annotation with browser config options
- Update injector to handle annotation

---

### Phase 3: Enhanced Target Abstraction

**Goal:** Rich locator strategies matching WebDriver version.

#### 3.1 Target Hierarchy

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/targets/TargetResolutionTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class TargetResolutionTest {

    @CastMember Actor alice;

    static Target LOGIN_BUTTON = Target.the("login button").locatedBy("#login-btn");
    static Target MENU_ITEM = Target.the("menu item").locatedBy("role=menuitem[name='Settings']");
    static Target LINK_TEXT = Target.the("link").locatedBy("text=Click here");

    @Test
    void should_resolve_css_selector() {
        alice.attemptsTo(
            Open.url("https://example.com/login"),
            Click.on(LOGIN_BUTTON)
        );
    }

    @Test
    void should_resolve_role_selector() {
        alice.attemptsTo(
            Open.url("https://example.com/dashboard"),
            Click.on(MENU_ITEM)
        );
    }

    @Test
    void should_resolve_text_selector() {
        alice.attemptsTo(
            Open.url("https://example.com"),
            Click.on(LINK_TEXT)
        );
    }
}
```

**Implementation:**
1. Extend `Target` class hierarchy:
   - `PlaywrightTarget` (base)
   - `CssOrXPathTarget` (CSS/XPath selectors)
   - `RoleTarget` (Playwright role selectors)
   - `TextTarget` (text-based selectors)
   - `LambdaTarget` (custom locator functions)

2. Add `TargetBuilder` with fluent API

#### 3.2 Parameterized Targets

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/targets/ParameterizedTargetTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class ParameterizedTargetTest {

    @CastMember Actor alice;

    static Target ITEM_BY_NAME = Target.the("{0} item").locatedBy("[data-item='{0}']");

    @Test
    void should_resolve_parameterized_selector() {
        alice.attemptsTo(
            Open.url("https://example.com/items"),
            Click.on(ITEM_BY_NAME.of("Product A")),
            Ensure.that(ITEM_BY_NAME.of("Product A")).isVisible()
        );
    }
}
```

**Implementation:**
- Add `of(String... parameters)` method to Target
- String substitution in selector

#### 3.3 Nested Targets & Chaining

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/targets/NestedTargetTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class NestedTargetTest {

    @CastMember Actor alice;

    static Target CARD = Target.the("card").locatedBy(".card");
    static Target CARD_TITLE = Target.the("card title").locatedBy(".title");

    @Test
    void should_find_element_inside_container() {
        alice.attemptsTo(
            Open.url("https://example.com/cards"),
            Click.on(CARD_TITLE.inside(CARD.of("first")))
        );
    }

    @Test
    void should_chain_locators() {
        Target firstCardTitle = CARD.find(CARD_TITLE);

        alice.attemptsTo(
            Open.url("https://example.com/cards")
        );

        String title = alice.asksFor(Text.of(firstCardTitle));
        assertThat(title).isNotEmpty();
    }
}
```

**Implementation:**
- Add `inside(Target container)` method
- Add `find(Target child)` method
- Use Playwright's `locator().locator()` chaining

#### 3.4 Frame Support

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/targets/FrameTargetTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class FrameTargetTest {

    @CastMember Actor alice;

    static Target IFRAME_CONTENT = Target.the("iframe content")
        .inFrame("iframe#editor")
        .locatedBy("#content");

    @Test
    void should_interact_with_element_in_iframe() {
        alice.attemptsTo(
            Open.url("https://example.com/editor"),
            Enter.theValue("Hello").into(IFRAME_CONTENT)
        );

        String text = alice.asksFor(Text.of(IFRAME_CONTENT));
        assertThat(text).contains("Hello");
    }
}
```

**Implementation:**
- Add `inFrame(String frameSelector)` method to Target
- Use Playwright's `page.frameLocator()` API

---

### Phase 4: Core Interactions

**Goal:** Implement essential interactions with proper reporting.

#### 4.1 Navigation Interactions

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/interactions/NavigationTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class NavigationTest {

    @CastMember Actor alice;

    @Test
    void should_navigate_to_url() {
        alice.attemptsTo(
            Open.url("https://example.com"),
            Ensure.that("h1").isVisible()
        );
    }

    @Test
    void should_navigate_back_and_forward() {
        alice.attemptsTo(
            Open.url("https://example.com"),
            Open.url("https://example.com/about"),
            Navigate.back(),
            Ensure.thatTheCurrentUrl().contains("example.com"),
            Navigate.forward(),
            Ensure.thatTheCurrentUrl().contains("/about")
        );
    }

    @Test
    void should_refresh_page() {
        alice.attemptsTo(
            Open.url("https://example.com"),
            Navigate.refresh()
        );
    }
}
```

**Interactions to implement:**
- `Navigate.back()`
- `Navigate.forward()`
- `Navigate.refresh()`
- `Navigate.toUrl(String url)`

#### 4.2 Mouse Interactions

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/interactions/MouseInteractionsTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class MouseInteractionsTest {

    @CastMember Actor alice;

    static Target HOVER_AREA = Target.the("hover area").locatedBy("#hover-target");
    static Target DRAG_SOURCE = Target.the("drag source").locatedBy("#drag-source");
    static Target DROP_TARGET = Target.the("drop target").locatedBy("#drop-target");

    @Test
    void should_hover_over_element() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/hovers"),
            Hover.over(".figure"),
            Ensure.that(".figcaption").isVisible()
        );
    }

    @Test
    void should_drag_and_drop() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/drag_and_drop"),
            Drag.from("#column-a").to("#column-b")
        );
    }

    @Test
    void should_right_click() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/context_menu"),
            RightClick.on("#hot-spot")
        );
    }
}
```

**Interactions to implement:**
- `Hover.over(Target/String)`
- `Drag.from(Target/String).to(Target/String)`
- `RightClick.on(Target/String)`
- `DoubleClick.on(Target/String)` (exists, verify works)

#### 4.3 Form Interactions

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/interactions/FormInteractionsTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class FormInteractionsTest {

    @CastMember Actor alice;

    @Test
    void should_clear_input_field() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/inputs"),
            Enter.theValue("123").into("input[type='number']"),
            Clear.field("input[type='number']"),
            Ensure.that("input[type='number']").currentValue("")
        );
    }

    @Test
    void should_check_and_uncheck_checkbox() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/checkboxes"),
            Check.checkbox("#checkboxes input:first-child"),
            Ensure.that("#checkboxes input:first-child").isChecked(),
            Uncheck.checkbox("#checkboxes input:first-child"),
            Ensure.that("#checkboxes input:first-child").isNotChecked()
        );
    }

    @Test
    void should_enter_text_and_press_keys() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/login"),
            Enter.theValue("tomsmith").into("#username").thenHit(Keys.TAB),
            Enter.theValue("SuperSecretPassword!").into("#password").thenHit(Keys.ENTER)
        );
    }
}
```

**Interactions to implement:**
- `Clear.field(Target/String)`
- `Uncheck.checkbox(Target/String)`
- Extend `Enter` with `.thenHit(Keys...)` support

#### 4.4 Alert Handling

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/interactions/AlertHandlingTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class AlertHandlingTest {

    @CastMember Actor alice;

    @Test
    void should_accept_alert() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/javascript_alerts"),
            Click.on("button[onclick='jsAlert()']"),
            AcceptAlert.dialog()
        );
    }

    @Test
    void should_dismiss_alert() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/javascript_alerts"),
            Click.on("button[onclick='jsConfirm()']"),
            DismissAlert.dialog()
        );
    }

    @Test
    void should_enter_text_in_prompt() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/javascript_alerts"),
            Click.on("button[onclick='jsPrompt()']"),
            EnterIntoAlert.theText("Hello"),
            AcceptAlert.dialog()
        );
    }
}
```

**Interactions to implement:**
- `AcceptAlert.dialog()`
- `DismissAlert.dialog()`
- `EnterIntoAlert.theText(String)`

#### 4.5 JavaScript Execution

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/interactions/JavaScriptTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class JavaScriptTest {

    @CastMember Actor alice;

    @Test
    void should_execute_javascript() {
        alice.attemptsTo(
            Open.url("https://example.com"),
            ExecuteJavaScript.async("document.title = 'Modified'")
        );

        String title = alice.asksFor(TheWebPage.title());
        assertThat(title).isEqualTo("Modified");
    }

    @Test
    void should_evaluate_javascript_and_return_value() {
        alice.attemptsTo(
            Open.url("https://example.com")
        );

        Object result = alice.asksFor(JavaScript.expression("1 + 1"));
        assertThat(result).isEqualTo(2);
    }
}
```

**Interactions to implement:**
- `ExecuteJavaScript.async(String script)`
- `JavaScript.expression(String)` question

---

### Phase 5: Core Questions

**Goal:** Implement essential questions for querying page state.

#### 5.1 Element State Questions

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/questions/ElementStateQuestionsTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class ElementStateQuestionsTest {

    @CastMember Actor alice;

    @Test
    void should_check_element_presence() {
        alice.attemptsTo(
            Open.url("https://example.com")
        );

        Boolean present = alice.asksFor(Presence.of("h1"));
        Boolean absent = alice.asksFor(Absence.of(".nonexistent"));

        assertThat(present).isTrue();
        assertThat(absent).isTrue();
    }

    @Test
    void should_check_element_enabled_state() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/dynamic_controls")
        );

        Boolean enabled = alice.asksFor(Enabled.of("#input-example input"));
        assertThat(enabled).isFalse(); // Initially disabled
    }

    @Test
    void should_get_element_count() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/checkboxes")
        );

        Integer count = alice.asksFor(Count.of("#checkboxes input"));
        assertThat(count).isEqualTo(2);
    }
}
```

**Questions to implement:**
- `Presence.of(Target/String)` - element exists in DOM
- `Absence.of(Target/String)` - element doesn't exist
- `Enabled.of(Target/String)` - element is enabled
- `Disabled.of(Target/String)` - element is disabled
- `Count.of(Target/String)` - number of matching elements

#### 5.2 Page Questions

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/questions/PageQuestionsTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class PageQuestionsTest {

    @CastMember Actor alice;

    @Test
    void should_get_page_title() {
        alice.attemptsTo(
            Open.url("https://example.com")
        );

        String title = alice.asksFor(TheWebPage.title());
        assertThat(title).isNotEmpty();
    }

    @Test
    void should_get_current_url() {
        alice.attemptsTo(
            Open.url("https://example.com/path?query=value")
        );

        String url = alice.asksFor(TheWebPage.currentUrl());
        assertThat(url).contains("example.com");
    }

    @Test
    void should_get_page_source() {
        alice.attemptsTo(
            Open.url("https://example.com")
        );

        String source = alice.asksFor(TheWebPage.source());
        assertThat(source).contains("<html");
    }
}
```

**Questions to implement:**
- `TheWebPage.title()` (exists, verify)
- `TheWebPage.currentUrl()`
- `TheWebPage.source()`

#### 5.3 CSS and Style Questions

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/questions/StyleQuestionsTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class StyleQuestionsTest {

    @CastMember Actor alice;

    @Test
    void should_get_css_value() {
        alice.attemptsTo(
            Open.url("https://example.com")
        );

        String color = alice.asksFor(CSSValue.of("h1").named("color"));
        assertThat(color).isNotEmpty();
    }

    @Test
    void should_check_element_classes() {
        alice.attemptsTo(
            Open.url("https://example.com")
        );

        Boolean hasClass = alice.asksFor(ElementClass.of("body").contains("some-class"));
        assertThat(hasClass).isNotNull();
    }
}
```

**Questions to implement:**
- `CSSValue.of(Target/String).named(String property)`
- `ElementClass.of(Target/String).contains(String className)`

---

### Phase 6: Wait Strategies

**Goal:** Implement robust waiting mechanisms.

#### 6.1 Explicit Waits

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/waits/ExplicitWaitTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class ExplicitWaitTest {

    @CastMember Actor alice;

    @Test
    void should_wait_for_element_to_be_visible() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/dynamic_loading/2"),
            Click.on("#start button"),
            WaitUntil.the("#finish h4").isVisible(),
            Ensure.that("#finish h4").hasText("Hello World!")
        );
    }

    @Test
    void should_wait_for_element_to_disappear() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/dynamic_loading/1"),
            Click.on("#start button"),
            WaitUntil.the("#loading").isHidden()
        );
    }

    @Test
    void should_wait_with_custom_timeout() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/dynamic_loading/2"),
            Click.on("#start button"),
            WaitUntil.the("#finish h4").isVisible().forNoMoreThan(Duration.ofSeconds(30))
        );
    }
}
```

**Interactions to implement:**
- `WaitUntil.the(Target/String).isVisible()`
- `WaitUntil.the(Target/String).isHidden()`
- `WaitUntil.the(Target/String).isEnabled()`
- `WaitUntil.the(Target/String).hasText(String)`
- `.forNoMoreThan(Duration)` modifier

#### 6.2 Eventual Assertions

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/waits/EventualAssertionTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class EventualAssertionTest {

    @CastMember Actor alice;

    @Test
    void should_eventually_see_element_appear() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/dynamic_loading/2"),
            Click.on("#start button")
        );

        alice.should(
            eventually(seeThat(Text.of("#finish h4"), containsString("Hello")))
                .waitingForNoLongerThan(Duration.ofSeconds(10))
        );
    }
}
```

**Implementation:**
- Verify `eventually()` from serenity-screenplay works with Playwright questions

---

### Phase 7: Enhanced Assertions (Ensure)

**Goal:** Rich, fluent assertions.

#### 7.1 Ensure Enhancements

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/assertions/EnsureTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class EnsureTest {

    @CastMember Actor alice;

    @Test
    void should_ensure_text_content() {
        alice.attemptsTo(
            Open.url("https://example.com"),
            Ensure.that("h1").hasText("Example Domain"),
            Ensure.that("h1").containsText("Example"),
            Ensure.that("h1").hasTextMatching("Example.*")
        );
    }

    @Test
    void should_ensure_attribute_values() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/login"),
            Ensure.that("#username").hasAttribute("name", "username"),
            Ensure.that("form").hasAttribute("action").containing("/authenticate")
        );
    }

    @Test
    void should_ensure_url_state() {
        alice.attemptsTo(
            Open.url("https://example.com/path?q=search"),
            Ensure.thatTheCurrentUrl().contains("/path"),
            Ensure.thatTheCurrentUrl().hasParameter("q", "search")
        );
    }

    @Test
    void should_ensure_page_title() {
        alice.attemptsTo(
            Open.url("https://example.com"),
            Ensure.thatThePageTitle().isEqualTo("Example Domain")
        );
    }
}
```

**Assertions to implement:**
- `Ensure.that(selector).hasText(String)`
- `Ensure.that(selector).containsText(String)`
- `Ensure.that(selector).hasTextMatching(String regex)`
- `Ensure.that(selector).hasAttribute(String name, String value)`
- `Ensure.that(selector).hasAttribute(String name).containing(String)`
- `Ensure.thatTheCurrentUrl().contains(String)`
- `Ensure.thatTheCurrentUrl().hasParameter(String, String)`
- `Ensure.thatThePageTitle().isEqualTo(String)`

---

### Phase 8: Reporting Integration

**Goal:** Ensure all interactions generate proper Serenity reports.

#### 8.1 Step Annotations

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/reporting/ReportingTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class ReportingTest {

    @CastMember Actor alice;

    @Test
    void interactions_should_appear_in_report() {
        alice.attemptsTo(
            Open.url("https://example.com"),
            Click.on("a"),
            Enter.theValue("test").into("input")
        );

        // Verify steps are recorded
        TestOutcome outcome = StepEventBus.getParallelEventBus()
            .getBaseStepListener()
            .latestTestOutcome()
            .orElseThrow();

        assertThat(outcome.getTestSteps()).hasSizeGreaterThan(0);
    }

    @Test
    void screenshots_should_be_captured() {
        alice.attemptsTo(
            Open.url("https://example.com"),
            Click.on("a")
        );

        TestOutcome outcome = StepEventBus.getParallelEventBus()
            .getBaseStepListener()
            .latestTestOutcome()
            .orElseThrow();

        // With FOR_EACH_ACTION screenshot mode
        assertThat(outcome.getScreenshots()).isNotEmpty();
    }
}
```

**Implementation:**
- Verify all interactions have proper `@Step` annotations
- Ensure `notifyScreenChange()` is called after user-facing actions
- Leverage `PlaywrightStepListener` for screenshot capture

---

### Phase 9: Advanced Features

**Goal:** Advanced Playwright-specific capabilities.

#### 9.1 Network Interception

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/network/NetworkInterceptionTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class NetworkInterceptionTest {

    @CastMember Actor alice;

    @Test
    void should_intercept_and_mock_api_response() {
        alice.attemptsTo(
            InterceptNetwork.forUrl("**/api/users")
                .andRespondWith("{\"users\": []}"),
            Open.url("https://example.com/users")
        );

        // Verify mocked response was used
    }

    @Test
    void should_wait_for_network_response() {
        alice.attemptsTo(
            Open.url("https://example.com"),
            Click.on("#load-data"),
            WaitForResponse.matching("**/api/data")
        );
    }
}
```

**Interactions to implement:**
- `InterceptNetwork.forUrl(String pattern).andRespondWith(String body)`
- `WaitForResponse.matching(String urlPattern)`

#### 9.2 Multiple Pages/Tabs

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/pages/MultiplePageTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class MultiplePageTest {

    @CastMember Actor alice;

    @Test
    void should_handle_popup_window() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/windows"),
            Click.on("a[href='/windows/new']"),
            SwitchTo.newWindow(),
            Ensure.that("h3").hasText("New Window"),
            SwitchTo.originalWindow()
        );
    }
}
```

**Interactions to implement:**
- `SwitchTo.newWindow()`
- `SwitchTo.originalWindow()`
- `SwitchTo.windowWithTitle(String)`

#### 9.3 Cookie Management

**Test first:**

```java
// src/test/java/net/serenitybdd/screenplay/playwright/cookies/CookieManagementTest.java
@ExtendWith(SerenityJUnit5Extension.class)
public class CookieManagementTest {

    @CastMember Actor alice;

    @Test
    void should_add_and_read_cookies() {
        alice.attemptsTo(
            Open.url("https://example.com"),
            AddCookie.named("test").withValue("value"),
        );

        String cookieValue = alice.asksFor(CookieValue.named("test"));
        assertThat(cookieValue).isEqualTo("value");
    }

    @Test
    void should_delete_cookies() {
        alice.attemptsTo(
            Open.url("https://example.com"),
            DeleteCookies.all()
        );
    }
}
```

**Interactions/Questions to implement:**
- `AddCookie.named(String).withValue(String)`
- `DeleteCookies.all()`
- `DeleteCookies.named(String)`
- `CookieValue.named(String)` question

---

## Implementation Priority

### High Priority (Phase 1-4)
1. **Module integration** - Foundation for everything else
2. **HasTeardown** - Proper resource cleanup
3. **JUnit 5 integration** - Modern test framework support
4. **Core interactions** - Essential for any test

### Medium Priority (Phase 5-7)
5. **Core questions** - Needed for assertions
6. **Wait strategies** - Robust test execution
7. **Enhanced assertions** - Better readability

### Lower Priority (Phase 8-9)
8. **Reporting verification** - Quality assurance
9. **Advanced features** - Nice-to-have capabilities

---

## File Structure After Migration

```
serenity-screenplay-playwright/
├── src/main/java/net/serenitybdd/screenplay/playwright/
│   ├── abilities/
│   │   ├── BrowseTheWebWithPlaywright.java (enhanced)
│   │   ├── ActorCannotUsePlaywrightException.java
│   │   └── InvalidPlaywrightBrowserType.java
│   │
│   ├── annotations/
│   │   └── PlaywrightCastMember.java (new)
│   │
│   ├── injectors/
│   │   └── PlaywrightCapableActorInjector.java (new)
│   │
│   ├── targets/
│   │   ├── PlaywrightTarget.java (new base)
│   │   ├── Target.java (enhanced)
│   │   ├── TargetBuilder.java (new)
│   │   ├── CssOrXPathTarget.java (new)
│   │   ├── RoleTarget.java (new)
│   │   └── LambdaTarget.java (new)
│   │
│   ├── interactions/
│   │   ├── Click.java (enhanced)
│   │   ├── DoubleClick.java
│   │   ├── RightClick.java (new)
│   │   ├── Hover.java (new)
│   │   ├── Drag.java (new)
│   │   ├── Enter.java (enhanced with thenHit)
│   │   ├── Clear.java (new)
│   │   ├── Check.java
│   │   ├── Uncheck.java (new)
│   │   ├── Open.java
│   │   ├── Navigate.java (new)
│   │   ├── Press.java
│   │   ├── Upload.java
│   │   ├── WaitFor.java (enhanced)
│   │   ├── WaitUntil.java (new)
│   │   ├── AcceptAlert.java (new)
│   │   ├── DismissAlert.java (new)
│   │   ├── EnterIntoAlert.java (new)
│   │   ├── ExecuteJavaScript.java (new)
│   │   ├── InterceptNetwork.java (new)
│   │   ├── SwitchTo.java (new)
│   │   ├── AddCookie.java (new)
│   │   ├── DeleteCookies.java (new)
│   │   └── SelectFromOptions.java
│   │
│   ├── questions/
│   │   ├── Text.java (enhanced)
│   │   ├── Attribute.java
│   │   ├── Visibility.java
│   │   ├── Presence.java (new)
│   │   ├── Absence.java (new)
│   │   ├── Enabled.java (new)
│   │   ├── Disabled.java (new)
│   │   ├── Count.java (new)
│   │   ├── TheWebPage.java (enhanced)
│   │   ├── CSSValue.java (new)
│   │   ├── ElementClass.java (new)
│   │   ├── JavaScript.java (new)
│   │   ├── CookieValue.java (new)
│   │   └── SelectOptions.java
│   │
│   ├── assertions/
│   │   ├── Ensure.java (enhanced)
│   │   ├── EnsureWithTimeout.java (fixed)
│   │   └── UrlAssertions.java (new)
│   │
│   └── Photographer.java
│
├── src/main/resources/META-INF/services/
│   └── net.serenitybdd.model.di.DependencyInjector (new)
│
└── src/test/java/net/serenitybdd/screenplay/playwright/
    ├── abilities/
    ├── injectors/
    ├── targets/
    ├── interactions/
    ├── questions/
    ├── assertions/
    ├── waits/
    ├── reporting/
    └── integration/
```

---

## Testing Strategy

### Unit Tests
- Mock Playwright objects where possible
- Test Target resolution logic
- Test factory methods and builders

### Integration Tests
- Use live test websites (the-internet.herokuapp.com, example.com)
- Verify full Screenplay flow
- Test report generation

### Parallel Execution Tests
- Verify ThreadLocal isolation
- Test multiple actors concurrently

---

## Migration Checklist

- [ ] Phase 1.1: Update pom.xml dependencies
- [ ] Phase 1.2: Implement HasTeardown
- [ ] Phase 2.1: Create PlaywrightCapableActorInjector
- [ ] Phase 2.2: Create @PlaywrightCastMember
- [ ] Phase 3.1: Implement Target hierarchy
- [ ] Phase 3.2: Add parameterized targets
- [ ] Phase 3.3: Add nested targets & chaining
- [ ] Phase 3.4: Add frame support
- [ ] Phase 4.1: Navigation interactions
- [ ] Phase 4.2: Mouse interactions
- [ ] Phase 4.3: Form interactions
- [ ] Phase 4.4: Alert handling
- [ ] Phase 4.5: JavaScript execution
- [ ] Phase 5.1: Element state questions
- [ ] Phase 5.2: Page questions
- [ ] Phase 5.3: CSS and style questions
- [ ] Phase 6.1: Explicit waits
- [ ] Phase 6.2: Eventual assertions
- [ ] Phase 7.1: Ensure enhancements
- [ ] Phase 8.1: Verify reporting
- [ ] Phase 9.1: Network interception
- [ ] Phase 9.2: Multiple pages/tabs
- [ ] Phase 9.3: Cookie management

---

## Notes

- All tests should run in headless mode by default for CI
- Use `playwright.headless=false` for local debugging
- Leverage Playwright's built-in auto-waiting where possible
- Prefer Playwright's locator strategies (text, role, aria) over CSS/XPath when semantic
