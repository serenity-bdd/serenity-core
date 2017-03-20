package net.thucydides.core.webdriver.stubs;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.core.time.InternalSystemClock;
import net.thucydides.core.webdriver.javascript.JavascriptExecutorFacade;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.support.ui.Duration;
import org.openqa.selenium.support.ui.Wait;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WebElementFacadeStub implements WebElementFacade {


    protected WebElement getElement() {
        return this;
    }

    protected JavascriptExecutorFacade getJavascriptExecutorFacade() {
        return new JavascriptExecutorFacade() {

            @Override
            protected Gson getGson() {
                return null;
            }

            @Override
            public void registerTypeAdapter(Type type, Object typeAdapter) {
            }

            @Override
            public Object executeScript(String script) {
                return null;
            }

            @Override
            public Object executeScript(String script, Object... params) {
                return null;
            }

            @Override
            public <T> T deserializeScriptResultAs(Class<T> classOfT, String script, Map<String, Object> injectedFields, Object... params) throws IllegalAccessException {
                return null;
            }

            @Override
            public <T> T deserializeScriptResultAs(Class<T> classOfT, String script, Object... params) throws IllegalAccessException {
                return null;
            }

            @Override
            public <T> List<T> deserializeScriptResultAsListOf(String script, Object... params) {
                return null;
            }
        };
    }

    protected InternalSystemClock getClock() {
        return new InternalSystemClock();
    }


    @Override
    public WebElementFacade then(String xpathOrCssSelector) {
        return this;
    }

    @Override
    public WebElementFacade findBy(String xpathOrCssSelector) {
        return this;

    }


    @Override
    public List<WebElementFacade> thenFindAll(String xpathOrCssSelector) {
        return Lists.newArrayList();
    }

    private List<WebElementFacade> webElementFacadesFrom(List<WebElement> nestedElements) {
        return Lists.newArrayList();
    }

    @Override
    public WebElementFacade findBy(By selector) {
        return this;
    }

    @Override
    public WebElementFacade find(By bySelector) {
        return this;
    }

    @Override
    public WebElementFacade then(By bySelector) {
        return this;
    }

    @Override
    public String getAttribute(String name) {
        return "";
    }

    @Override
    public List<WebElementFacade> thenFindAll(By selector) {
        return Lists.newArrayList();
    }

    @Override
    public long getImplicitTimeoutInMilliseconds() {
        return 0;
    }

    @Override
    public WebElementFacade withTimeoutOf(int timeout, TimeUnit unit) {
        return this;
    }

    /**
     * Is this web element present and visible on the screen
     * This method will not throw an exception if the element is not on the screen at all.
     * If the element is not visible, the method will wait a bit to see if it appears later on.
     */
    @Override
    public boolean isVisible() {

        return false;
    }

    /**
     * Convenience method to chain method calls more fluently.
     */
    @Override
    public WebElementFacade and() {
        return this;
    }

    /**
     * Convenience method to chain method calls more fluently.
     */
    @Override
    public WebElementFacade then() {
        return this;
    }

    /**
     * Is this web element present and visible on the screen
     * This method will not throw an exception if the element is not on the screen at all.
     * The method will fail immediately if the element is not visible on the screen.
     * There is a little black magic going on here - the web element class will detect if it is being called
     * by a method called "isCurrently*" and, if so, fail immediately without waiting as it would normally do.
     */
    @Override
    public boolean isCurrentlyVisible() {
        return false;
    }

    @Override
    public boolean isCurrentlyEnabled() {
        return false;
    }

    /**
     * Checks whether a web element is visible.
     * Throws an AssertionError if the element is not rendered.
     */
    @Override
    public void shouldBeVisible() {
    }

    /**
     * Checks whether a web element is visible.
     * Throws an AssertionError if the element is not rendered.
     */
    @Override
    public void shouldBeCurrentlyVisible() {
    }

    /**
     * Checks whether a web element is not visible.
     * Throws an AssertionError if the element is not rendered.
     */
    @Override
    public void shouldNotBeVisible() {
    }

    /**
     * Checks whether a web element is not visible straight away.
     * Throws an AssertionError if the element is not rendered.
     */
    @Override
    public void shouldNotBeCurrentlyVisible() {
    }

    /**
     * Does this element currently have the focus.
     */
    @Override
    public boolean hasFocus() {
        return false;
    }

    /**
     * Does this element contain a given text?
     */
    @Override
    public boolean containsText(final String value) {
        return false;
    }

    @Override
    public boolean containsValue(String value) {
        return false;
    }

    /**
     * Does this element exactly match  given text?
     */
    @Override
    public boolean containsOnlyText(final String value) {
        return false;
    }

    /**
     * Does this dropdown contain the specified value.
     */
    @Override
    public boolean containsSelectOption(final String value) {
        return false;
    }

    @Override
    public List<String> getSelectOptions() {
        return Lists.newArrayList();
    }

    /**
     * Check that an element contains a text value
     *
     * @param textValue
     */
    @Override
    public void shouldContainText(String textValue) {
    }

    /**
     * Check that an element exactly matches a text value
     *
     * @param textValue
     */
    @Override
    public void shouldContainOnlyText(String textValue) {
    }

    @Override
    public void shouldContainSelectedOption(String textValue) {
    }

    /**
     * Check that an element does not contain a text value
     *
     * @param textValue
     */
    @Override
    public void shouldNotContainText(String textValue) {
    }

    @Override
    public void shouldBeEnabled() {
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void shouldNotBeEnabled() {
    }

    /**
     * Type a value into a field, making sure that the field is empty first.
     *
     * @param value
     */
    @Override
    public WebElementFacade type(final String value) {
        return this;
    }

    /**
     * Type a value into a field and then press Enter, making sure that the field is empty first.
     *
     * @param value
     */
    @Override
    public WebElementFacade typeAndEnter(final String value) {
        return this;
    }

    /**
     * Type a value into a field and then press TAB, making sure that the field is empty first.
     * This currently is not supported by all browsers, notably Firefox.
     *
     * @param value
     */
    @Override
    public WebElementFacade typeAndTab(final String value) {
        return this;
    }

    @Override
    public void setWindowFocus() {
    }

    @Override
    public WebElementFacade deselectAll() {
        return this;
    }

    @Override
    public WebElementFacade deselectByVisibleText(String label) {
        return this;
    }

    @Override
    public WebElementFacade deselectByValue(String value) {
        return this;
    }

    @Override
    public WebElementFacade deselectByIndex(int indexValue) {
        return this;
    }

    @Override
    public WebElementFacade selectByVisibleText(final String label) {
        return this;
    }

    @Override
    public String getSelectedVisibleTextValue() {
        return "";
    }

    @Override
    public WebElementFacade selectByValue(String value) {
        return this;
    }

    @Override
    public String getSelectedValue() {
        return "";
    }

    @Override
    public WebElementFacade selectByIndex(int indexValue) {
        return this;
    }

    private void waitUntilElementAvailable() {
    }

    private boolean driverIsDisabled() {
        return false;
    }

    public boolean isPresent() {
        return false;
    }

    @Override
    public void shouldBePresent() {
    }

    @Override
    public void shouldNotBePresent() {
    }

    @Override
    public WebElementFacade waitUntilVisible() {
        return this;
    }

    @Override
    public WebElementFacade waitUntilPresent() {
        return this;
    }


    @Override
    public Wait<WebDriver> waitForCondition() {
        return new Wait<WebDriver>() {

            @Override
            public <T> T until(Function<? super WebDriver, T> function) {
                return null;
            }
        };
    }

    @Override
    public WebElementFacade waitUntilNotVisible() {
        return this;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public WebElementFacade waitUntilClickable() {
        return null;
    }

    @Override
    public WebElementFacade waitUntilEnabled() {
        return this;
    }

    @Override
    public WebElementFacade waitUntilDisabled() {
        return this;
    }

    @Override
    public String getTextValue() {
        return "";
    }

    @Override
    public WebElementState expect(String errorMessage) {
        return this;
    }

    protected WebElementState expectingErrorMessage(String errorMessage) {
        return this;
    }

    /**
     * Wait for an element to be visible and enabled, and then click on it.
     */
    @Override
    public void click() {
    }

    @Override
    public void clear() {
    }

    @Override
    public String toString() {
        return "";
    }

    public void submit() {
    }

    public void sendKeys(CharSequence... keysToSend) {
    }

    public String getTagName() {
        return "";
    }

    public List<WebElement> findElements(By by) {
        return Lists.newArrayList();
    }

    public WebElement findElement(By by) {
        return this;
    }

    public boolean isDisplayed() {
        return false;
    }

    public Point getLocation() {
        return new Point(0, 0);
    }

    public Dimension getSize() {
        return new Dimension(0, 0);
    }

    @Override
    public Rectangle getRect() {
        return new Rectangle(0,0,0,0);
    }

    public String getCssValue(String propertyName) {
        return "";
    }

    public WebElement getWrappedElement() {
        return this;
    }

    @Override
    public Coordinates getCoordinates() {
        return new Coordinates() {

            @Override
            public Point onScreen() {
                return new Point(0, 0);
            }

            @Override
            public Point inViewPort() {
                return new Point(0, 0);
            }

            @Override
            public Point onPage() {
                return new Point(0, 0);
            }

            @Override
            public Object getAuxiliary() {
                return new Point(0, 0);
            }
        };
    }


    @Override
    public WebElement findElementByAccessibilityId(String using) {
        return this;
    }

    @Override
    public List<WebElement> findElementsByAccessibilityId(String using) {
        return Lists.newArrayList();
    }

    @Override
    public WebElement findElementByAndroidUIAutomator(String using) {
        return this;
    }

    @Override
    public List<WebElement> findElementsByAndroidUIAutomator(String using) {
        return Lists.newArrayList();
    }

    @Override
    public WebElement findElementByIosUIAutomation(String using) {
        return this;
    }

    @Override
    public List<WebElement> findElementsByIosUIAutomation(String using) {
        return Lists.newArrayList();
    }

    @Override
    public void setImplicitTimeout(Duration implicitTimeout) {

    }

    @Override
    public Duration getCurrentImplicitTimeout() {
        return null;
    }

    @Override
    public Duration resetTimeouts() {
        return null;
    }

    @Override
    public boolean containsElements(By selector) {
        return false;
    }

    @Override
    public boolean containsElements(String xpathOrCssSelector) {
        return false;
    }

    @Override
    public void shouldContainElements(By selector) {
        
    }

    @Override
    public void shouldContainElements(String xpathOrCssSelector) {

    }

    @Override
    public boolean hasClass(String cssClassName) {
        return false;
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return null;
    }
}

