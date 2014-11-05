package net.thucydides.core.pages.integration;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.WebElementFacade;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;

@DefaultUrl("classpath:static-site/index.html")
public class StaticSitePage extends PageObject {

    @FindBy(name = "firstname")
    public WebElement firstName;

    @FindBy(name = "lastname")
    public WebElement lastName;

    @FindBy(name = "city")
    public WebElement city;

    @FindBy(name = "country")
    public WebElement country;

    @FindBy(name = "hiddenfield")
    public WebElement hiddenField;

    public WebElement csshiddenfield;

    public WebElement readonlyField;

    public WebElement doesNotExist;

    public WebElement textField;

    public WebElement checkbox;

    public WebElement radioButton1;
    public WebElement radioButton2;

    public WebElement selectedCheckbox;

    public WebElement buttonThatIsInitiallyDisabled;

    public WebElement buttonThatIsInitiallyEnabled;

    public WebElement placetitle;

    public WebElement dissapearingtext;

    @FindBy(id = "visible")
    public WebElement visibleTitle;

    @FindBy(id = "color")
    public WebElement colors;

    public WebElement elements;

    public WebElement grid;

    public WebElement emptylist;

    @FindBy(name = "fieldDoesNotExist")
    public WebElement fieldDoesNotExist;

    @FindBy(id = "emptyLabelID")
    public WebElement emptyLabel;

    @FindBy(id = "nonEmptyLabelID")
    public WebElement nonEmptyLabel;

    public WebElement focusmessage;

    public WebElement clients;

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
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (firstName.isDisplayed() && firstName.isEnabled());
            }
        };
    }

    public ExpectedCondition<Boolean> firstAndLastNameAreEnabled() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (firstName.isEnabled() && lastName.isEnabled());
            }
        };
    }

    public ExpectedCondition<Boolean> twoFieldsAreDisabled() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (!buttonThatIsInitiallyEnabled.isEnabled() && !readonlyField.isEnabled());
            }
        };
    }

}