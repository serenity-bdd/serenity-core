package net.serenitybdd.core.pages;

import java.util.List;

/**
 * This interface is a way of presenting state information about a web element without exposing (too much) of the
 * WebDriver API.
 */
public interface WebElementState {
    /**
     * Is this web element present and visible on the screen
     * This method will not throw an exception if the element is not on the screen at all.
     * If the element is not visible, the method will wait a bit to see if it appears later on.
     */
    boolean isVisible();

    /**
     * Is this web element present and visible on the screen
     * This method will not throw an exception if the element is not on the screen at all.
     * The method will fail immediately if the element is not visible on the screen.
     * There is a little black magic going on here - the web element class will detect if it is being called
     * by a method called "isCurrently*" and, if so, fail immediately without waiting as it would normally do.
     */
    boolean isCurrentlyVisible();

    boolean isCurrentlyEnabled();

    /**
     * Checks whether a web element is visible.
     * Throws an AssertionError if the element is not rendered.
     */
    WebElementState shouldBeVisible();

    /**
     * Checks whether a web element is visible.
     * Throws an AssertionError if the element is not rendered.
     */
    WebElementState shouldBeCurrentlyVisible();

    /**
     * Checks whether a web element is not visible.
     * Throws an AssertionError if the element is not rendered.
     */
    WebElementState shouldNotBeVisible();

    /**
     * Checks whether a web element is not visible straight away.
     * Throws an AssertionError if the element is not rendered.
     */
    WebElementState shouldNotBeCurrentlyVisible();


    /**
     * Does this element currently have the focus.
     */
    boolean hasFocus();

    /**
     * Does this element contain a given text?
     */
    boolean containsText(String value);

    /**
     * Does this element contain a given value attribute?
     */
    boolean containsValue(String value);

    /**
     * Does this element exactly match  given text?
     */
    boolean containsOnlyText(String value);

    /**
     * Does this dropdown contain the specified value.
     */
    boolean containsSelectOption(String value);

    /**
     * Check that an element contains a text value
     *
     * @param textValue
     */
    WebElementState shouldContainText(String textValue);

    /**
     * Check that an element exactly matches a text value
     *
     * @param textValue
     */
    WebElementState shouldContainOnlyText(String textValue);

    WebElementState shouldContainSelectedOption(String textValue);

    /**
     * Check that an element does not contain a text value
     *
     * @param textValue
     */
    WebElementState shouldNotContainText(String textValue);

    WebElementState shouldBeEnabled();

    boolean isEnabled();

    boolean isDisabled();

    WebElementState shouldNotBeEnabled();

    String getSelectedVisibleTextValue();

    List<String> getSelectedVisibleTexts();

    String getSelectedValue();

    List<String> getSelectedValues();

    List<String> getSelectOptions();

    boolean isPresent();

    WebElementState shouldBePresent();

    WebElementState shouldNotBePresent();

    WebElementState shouldBeSelected();

    WebElementState shouldNotBeSelected();

    boolean isSelected();

    String getTextValue();

    String getValue();

    String getText();

    String getAttribute(String name);

    WebElementState expect(String errorMessage);

    boolean isClickable();
}
