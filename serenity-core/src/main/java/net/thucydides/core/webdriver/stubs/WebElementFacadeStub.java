package net.thucydides.core.webdriver.stubs;

import net.serenitybdd.core.pages.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class WebElementFacadeStub implements WebElementFacade, WebElementState {

    public WebElement getElement() {
        return this;
    }

    @Override
    public ListOfWebElementFacades findNestedElementsMatching(ResolvableElement nestedElement) {
        return new ListOfWebElementFacades(new ArrayList<>());
    }

    @Override
    public WebElementFacade then(String xpathOrCssSelector) {
        return this;
    }

    @Override
    public WebElementFacade thenFind(String xpathOrCssSelector) {
        return this;
    }

    @Override
    public WebElementFacade thenFind(String xpathOrCssSelector, Object... arguments) {
        return this;
    }

    @Override
    public WebElementFacade then(String xpathOrCssSelector, Object... arguments) {
        return this;
    }

    @Override
    public WebElementFacade findBy(String xpathOrCssSelector) {
        return this;

    }


    @Override
    public WebElementFacade findBy(String xpathOrCssSelector, Object... arguments) {
        return this;
    }

    @Override
    public ListOfWebElementFacades thenFindAll(String xpathOrCssSelector) {
        return new ListOfWebElementFacades(new ArrayList<>());
    }

    @Override
    public ListOfWebElementFacades thenFindAll(String xpathOrCssSelector, Object... arguments) {
        return new ListOfWebElementFacades(new ArrayList<>());
    }

    private List<WebElementFacade> webElementFacadesFrom(List<WebElement> nestedElements) {
        return new ArrayList<>();
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
    public ListOfWebElementFacades thenFindAll(By... selector) {
        return new ListOfWebElementFacades(new ArrayList<>());
    }

    @Override
    public long getImplicitTimeoutInMilliseconds() {
        return 0;
    }

    @Override
    public WebElementFacade withTimeoutOf(int timeout, TimeUnit unit) {
        return this;
    }

    @Override
    public WebElementFacade withTimeoutOf(int timeout, TemporalUnit unit) {
        return this;
    }

    @Override
    public WebElementFacade withTimeoutOf(Duration duration) {
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
    public WebElementState shouldBeVisible() {
        return this;
    }

    /**
     * Checks whether a web element is visible.
     * Throws an AssertionError if the element is not rendered.
     */
    @Override
    public WebElementState shouldBeCurrentlyVisible() {
        return this;
    }

    /**
     * Checks whether a web element is not visible.
     * Throws an AssertionError if the element is not rendered.
     */
    @Override
    public WebElementState shouldNotBeVisible() {
        return this;
    }

    /**
     * Checks whether a web element is not visible straight away.
     * Throws an AssertionError if the element is not rendered.
     */
    @Override
    public WebElementState shouldNotBeCurrentlyVisible() {
        return this;
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
        return new ArrayList<>();
    }

    @Override
    public List<String> getSelectOptionValues() {
        return new ArrayList<>();
    }

    @Override
    public String getFirstSelectedOptionVisibleText() {
        return null;
    }

    @Override
    public List<String> getSelectedVisibleTexts() {
        return null;
    }

    @Override
    public String getFirstSelectedOptionValue() {
        return null;
    }

    @Override
    public List<String> getSelectedValues() {
        return null;
    }

    /**
     * Check that an element contains a text value
     *
     * @param textValue
     */
    @Override
    public WebElementState shouldContainText(String textValue) {
        return this;
    }

    /**
     * Check that an element exactly matches a text value
     *
     * @param textValue
     */
    @Override
    public WebElementState shouldContainOnlyText(String textValue) {
        return this;
    }

    @Override
    public WebElementState shouldContainSelectedOption(String textValue) {
        return this;
    }

    /**
     * Check that an element does not contain a text value
     *
     * @param textValue
     */
    @Override
    public WebElementState shouldNotContainText(String textValue) {
        return this;
    }

    @Override
    public WebElementState shouldBeEnabled() {
        return this;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public WebElementState shouldNotBeEnabled() {
        return this;
    }

    /**
     * Type a value into a field, making sure that the field is empty first.
     *
     * @param keysToSend
     */
    @Override
    public WebElementFacade type(CharSequence... keysToSend) {
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
    public FluentDropdownSelect select() {
        return new FluentDropdownSelect(new WebElementFacadeStub());
    }

    @Override
    public FluentDropdownDeselect deselect() {
        return new FluentDropdownDeselect(new WebElementFacadeStub());
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
    public WebElementState shouldBePresent() {
        return this;
    }

    @Override
    public WebElementState shouldNotBePresent() {
        return this;
    }

    @Override
    public WebElementState shouldBeSelected() {
        return this;
    }

    @Override
    public WebElementState shouldNotBeSelected() {
        return this;
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
    public String getTextContent() {
        return "";
    }

    @Override
    public boolean isDisabled() {
        return false;
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

    @Override
    public boolean isClickable() {
        return false;
    }

    protected WebElementState expectingErrorMessage(String errorMessage) {
        return this;
    }

    /**
     * Wait for an element to be visible and enabled, and then click on it.
     */
    @Override
    public void click() {}

    @Override
    public void click(ClickStrategy clickStrategy) {}

    @Override
    public void doubleClick() {

    }

    @Override
    public void contextClick() {

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
        return new ArrayList<>();
    }

    public WebElement findElement(By by) {
        return this;
    }

    public WebElement findElement(String by, String using) {
        return this;
    }

    public List findElements(String by, String using) {
        return new ArrayList();
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
        return new WrappedElementStub();
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
    public WebElementState shouldContainElements(By selector) {
        return this;
    }

    @Override
    public WebElementState shouldContainElements(String xpathOrCssSelector) {
        return this;
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

