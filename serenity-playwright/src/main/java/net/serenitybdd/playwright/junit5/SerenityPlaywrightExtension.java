package net.serenitybdd.playwright.junit5;

import com.microsoft.playwright.Page;
import net.serenitybdd.playwright.PlaywrightPageRegistry;
import net.serenitybdd.playwright.PlaywrightSerenity;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * JUnit 5 extension for Playwright integration with Serenity BDD.
 * <p>
 * This extension works in conjunction with Playwright's {@code @UsePlaywright} annotation
 * to automatically register Playwright Page instances with Serenity for screenshot capture.
 * </p>
 * <p>
 * Usage with @UsePlaywright:
 * <pre>
 * &#64;ExtendWith(SerenityJUnit5Extension.class)
 * &#64;ExtendWith(SerenityPlaywrightExtension.class)
 * &#64;UsePlaywright
 * class MyPlaywrightTest {
 *
 *     &#64;Test
 *     void testSomething(Page page) {
 *         // Page is automatically registered for screenshots
 *         page.navigate("https://example.com");
 *     }
 * }
 * </pre>
 * </p>
 * <p>
 * The extension can also auto-detect Page fields in test instances and register them:
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
        ParameterResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerenityPlaywrightExtension.class);
    private static final String PAGES_KEY = "playwright.pages";

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

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        // Support injection of Page parameters
        return Page.class.isAssignableFrom(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        // Return the current page if one is registered
        Page currentPage = PlaywrightSerenity.getCurrentPage();
        if (currentPage != null) {
            return currentPage;
        }
        // If no page is registered yet, return null and let @UsePlaywright handle it
        return null;
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
