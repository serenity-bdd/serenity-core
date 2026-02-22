package net.serenitybdd.playwright.junit5;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import net.serenitybdd.playwright.PlaywrightSerenity;
import org.junit.jupiter.api.extension.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * JUnit 5 extension for Playwright integration with Serenity BDD.
 * <p>
 * This extension works in conjunction with Playwright's {@code @UsePlaywright} annotation
 * to automatically register Playwright Page instances with Serenity for screenshot capture.
 * </p>
 * <p>
 * When {@code @UsePlaywright} is present, Playwright manages the full browser lifecycle
 * (Playwright, Browser, BrowserContext, Page) and this extension automatically intercepts
 * the Page instances injected into {@code @BeforeEach} and {@code @Test} methods to register
 * them with Serenity for screenshot capture at step boundaries.
 * </p>
 * <p>
 * Usage with @UsePlaywright and a custom OptionsFactory:
 * <pre>
 * &#64;ExtendWith(SerenityJUnit5Extension.class)
 * &#64;ExtendWith(SerenityPlaywrightExtension.class)
 * &#64;UsePlaywright(ChromeHeadlessOptions.class)
 * class MyPlaywrightTest {
 *
 *     &#64;Steps
 *     MySteps steps;
 *
 *     &#64;BeforeEach
 *     void setUp(Page page) {
 *         // Page is created and managed by Playwright
 *         // and automatically registered with Serenity for screenshots
 *     }
 *
 *     &#64;Test
 *     void testSomething(Page page) {
 *         page.navigate("https://example.com");
 *         steps.verifyPageLoaded(page);
 *     }
 * }
 * </pre>
 * </p>
 * <p>
 * The extension can also be used without {@code @UsePlaywright} by auto-detecting
 * Page fields in the test instance:
 * <pre>
 * &#64;ExtendWith(SerenityJUnit5Extension.class)
 * &#64;ExtendWith(SerenityPlaywrightExtension.class)
 * class MyPlaywrightTest {
 *
 *     private Page page;  // Will be auto-registered if initialized before test
 *
 *     &#64;BeforeEach
 *     void setup() {
 *         page = browser.newPage();
 *     }
 * }
 * </pre>
 * </p>
 *
 * @see PlaywrightSerenity
 * @see SerenityPlaywright
 */
public class SerenityPlaywrightExtension implements
        TestInstancePostProcessor,
        BeforeEachCallback,
        BeforeTestExecutionCallback,
        AfterEachCallback,
        ParameterResolver,
        InvocationInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerenityPlaywrightExtension.class);

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        // Store the test instance for later page detection
        getStore(context).put("testInstance", testInstance);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        // Clear any leftover pages from previous tests and reset listener flag
        PlaywrightSerenity.clear();
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        // Scan for Page fields right before test execution
        // (after @BeforeEach methods have had a chance to initialize pages)
        Object testInstance = getStore(context).get("testInstance");
        if (testInstance != null) {
            registerPagesFromTestInstance(testInstance);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        // Clean up registered pages and reset listener registration
        PlaywrightSerenity.clear();
    }

    // ---- ParameterResolver ----

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        // When @UsePlaywright is present, Playwright's own PageExtension handles Page parameter resolution.
        // We must not compete with it or JUnit 5 will throw ParameterResolutionException.
        if (hasUsePlaywrightAnnotation(extensionContext)) {
            return false;
        }
        return Page.class.isAssignableFrom(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        // Return the current page if one is registered (only called when @UsePlaywright is NOT present)
        return PlaywrightSerenity.getCurrentPage();
    }

    // ---- InvocationInterceptor ----
    // When @UsePlaywright is present, Playwright resolves Page parameters for @BeforeEach and @Test methods.
    // We intercept these invocations to register the Page with Serenity for screenshot capture.

    @Override
    public void interceptBeforeEachMethod(InvocationInterceptor.Invocation<Void> invocation,
                                          ReflectiveInvocationContext<Method> invocationContext,
                                          ExtensionContext extensionContext) throws Throwable {
        // Register Page arguments before @BeforeEach runs so that any step methods
        // called within setUp() will have screenshots captured
        registerPageArguments(invocationContext);
        invocation.proceed();
    }

    @Override
    public void interceptTestMethod(InvocationInterceptor.Invocation<Void> invocation,
                                    ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext extensionContext) throws Throwable {
        // Register Page arguments before the test method runs
        registerPageArguments(invocationContext);
        invocation.proceed();
    }

    // ---- Private helpers ----

    private void registerPageArguments(ReflectiveInvocationContext<Method> invocationContext) {
        for (Object arg : invocationContext.getArguments()) {
            if (arg instanceof Page) {
                Page page = (Page) arg;
                PlaywrightSerenity.registerPage(page);
                LOGGER.debug("Auto-registered Playwright Page from method parameter: {}",
                        invocationContext.getExecutable().getName());
            }
        }
    }

    private boolean hasUsePlaywrightAnnotation(ExtensionContext extensionContext) {
        return extensionContext.getTestClass()
                .map(clazz -> clazz.isAnnotationPresent(UsePlaywright.class))
                .orElse(false);
    }

    private void registerPagesFromTestInstance(Object testInstance) {
        List<Page> pages = findPageFields(testInstance);
        for (Page page : pages) {
            if (page != null) {
                PlaywrightSerenity.registerPage(page);
                LOGGER.debug("Auto-registered Playwright Page from field in test instance");
            }
        }
    }

    private List<Page> findPageFields(Object testInstance) {
        List<Page> pages = new ArrayList<>();
        Class<?> clazz = testInstance.getClass();

        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (Page.class.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(testInstance);
                        if (value instanceof Page) {
                            pages.add((Page) value);
                        }
                    } catch (IllegalAccessException e) {
                        LOGGER.debug("Could not access field {} for Page detection", field.getName());
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }

        return pages;
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass(), context.getUniqueId()));
    }
}
