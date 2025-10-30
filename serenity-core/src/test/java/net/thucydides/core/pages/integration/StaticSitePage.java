package net.thucydides.core.pages.integration;

import net.serenitybdd.annotations.DefaultUrl;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.List;

@DefaultUrl("classpath:static-site/index.html")
public class StaticSitePage extends PageObject {

    @FindBy(name = "firstname")
    public WebElementFacade firstName;

    @FindBy(name = "lastname")
    public WebElement lastName;

    @FindBy(name = "city")
    public WebElementFacade city;

    @FindBy(id="slow-loader")
    public WebElementFacade slowLoadingField;

    @FindBy(id="very-slow-loader")
    public WebElementFacade verySlowLoadingField;

    @FindBy(name = "country", timeoutInSeconds="5")
    public WebElementFacade country;

    @FindBy(name = "hiddenfield")
    public WebElementFacade hiddenField;

    public WebElement csshiddenfield;

    public WebElementFacade readonlyField;

    public WebElement doesNotExist;

    public WebElement textField;

    public WebElement checkbox;

    public WebElement radioButton1;
    public WebElement radioButton2;

    public WebElement selectedCheckbox;

    @FindBy(id="buttonThatIsInitiallyDisabled")
    public WebElementFacade initiallyDisabled;

    @FindBy(id="buttonThatIsInitiallyEnabled")
    public WebElementFacade initiallyEnabled;

    public WebElementFacade placetitle;

    public WebElementFacade dissapearingtext;

    @FindBy(id = "visible")
    public WebElement visibleTitle;

    @FindBy(id = "color")
    public WebElement colors;

    public WebElement elements;

    @FindBy(css="#elements option")
    public List<WebElementFacade> elementItems;

    @FindBy(css="#elements option")
    public WebElementFacade firstElementItem;

    public WebElement grid;

    public WebElement emptylist;

    @FindBy(name = "fieldDoesNotExist")
    public WebElementFacade fieldDoesNotExist;

    @FindBy(id = "emptyLabelID")
    public WebElement emptyLabel;

    @FindBy(id = "nonEmptyLabelID")
    public WebElement nonEmptyLabel;

    public WebElement focusmessage;

    public WebElementFacade clients;

    public WebElement clients_with_no_headings;

    public WebElement clients_with_nested_cells;

    public WebElement clients_with_extra_cells;

    public WebElement clients_with_missing_cells;

    public WebElement table_with_merged_cells;

    public WebElement table_with_empty_headers;

    public WebElement table_with_td_headers;

    public WebElement alertButton;

    public WebElementFacade alertButton() {
        return element(alertButton);
    }

    public void openAlert() {
        alertButton().click();
        waitABit(500);
    }

    public StaticSitePage(WebDriver driver) {
        super(driver);
    }

    public StaticSitePage(WebDriver driver, EnvironmentVariables environmentVariables) {
        super(driver, environmentVariables);
    }

    public StaticSitePage(WebDriver driver, int timeout) {
        super(driver, timeout);
    }

    public void setFirstName(String value) {
        element(firstName).type(value);
    }

    public void fieldDoesNotExistShouldNotBePresent() {
        element(fieldDoesNotExist).shouldNotBePresent();
    }

    public void fieldDoesNotExistShouldBePresent() {
        element(fieldDoesNotExist).shouldBePresent();
    }

    public void hiddenFieldShouldNotBePresent() {
        element(hiddenField).shouldNotBePresent();
    }

    public void fieldDoesNotExistShouldContainText(String value) {
        element(fieldDoesNotExist).shouldContainText(value);
    }

    public WebElementFacade firstName() {
        return element(firstName);
    }

    public void waitForFirstNameField() {
        waitForCondition().until(firstAndLastNameAreEnabled());
    }

    public ExpectedCondition<Boolean> firstNameIsVisibleAndDisabled() {
        return driver -> (firstName.isDisplayed() && firstName.isEnabled());
    }

    public ExpectedCondition<Boolean> firstAndLastNameAreEnabled() {
        return driver -> (firstName.isEnabled() && lastName.isEnabled());
    }

    public ExpectedCondition<Boolean> twoFieldsAreDisabled() {
        return driver -> (initiallyEnabled.isDisabled() && readonlyField.isDisabled());
    }

}
