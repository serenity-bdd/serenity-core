package net.thucydides.core.pages.integration;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;

@DefaultUrl("classpath:static-site/index.html")
public class StaticSitePageWithFacades extends PageObject {

        @FindBy(name = "firstname")
        protected WebElementFacade firstName;

        @FindBy(name = "firstname")
        protected net.thucydides.core.pages.WebElementFacade legacyFirstName;

        @FindBy(name = "lastname")
        protected WebElementFacade lastName;

        @FindBy(name = "city")
        protected WebElementFacade city;

        @FindBy(name = "country")
        protected WebElementFacade country;

        @FindBy(name = "hiddenfield")
        protected WebElementFacade hiddenField;

        @FindBy(name = "demo")
        protected WebElementFacade demoForm;

        protected WebElementFacade csshiddenfield;

        protected WebElementFacade readonlyField;

        protected WebElementFacade doesNotExist;

        protected WebElementFacade textField;

        protected WebElementFacade checkbox;

        protected WebElementFacade radioButton1;
        protected WebElementFacade radioButton2;

        protected WebElementFacade selectedCheckbox;

        protected WebElementFacade buttonThatIsInitiallyDisabled;

        protected WebElementFacade buttonThatIsInitiallyEnabled;

        protected WebElementFacade placetitle;

        protected WebElementFacade dissapearingtext;

        @FindBy(id = "visible")
        protected WebElementFacade visibleTitle;

        @FindBy(id = "color")
        protected WebElementFacade colors;

        protected WebElementFacade elements;

        protected WebElementFacade grid;

        protected WebElementFacade emptylist;

        @FindBy(name = "fieldDoesNotExist")
        protected WebElementFacade fieldDoesNotExist;

        @FindBy(id = "emptyLabelID")
        protected WebElementFacade emptyLabel;

        @FindBy(id = "nonEmptyLabelID")
        protected WebElementFacade nonEmptyLabel;

        protected WebElementFacade focusmessage;

        protected WebElementFacade clients;

        @FindBy(id = "clients")
        protected net.thucydides.core.pages.WebElementFacade  legacyClients;

        protected WebElementFacade clients_with_no_headings;

        protected WebElementFacade clients_with_nested_cells;

        protected WebElementFacade clients_with_extra_cells;

        protected WebElementFacade clients_with_missing_cells;

        protected WebElementFacade table_with_merged_cells;

        protected WebElementFacade table_with_empty_headers;

        protected WebElementFacade table_with_td_headers;

        protected WebElementFacade alertButton;

        public void openAlert() {
		    alertButton.click();
		    waitABit(500);
		}

        public StaticSitePageWithFacades(WebDriver driver, int timeout) {
            super(driver, timeout);
        }

        public void setFirstName(String value) {
            firstName.type(value);
        }

        public void fieldDoesNotExistShouldNotBePresent() {
            fieldDoesNotExist.shouldNotBePresent();
        }

        public void fieldDoesNotExistShouldBePresent() {
            fieldDoesNotExist.shouldBePresent();
        }

        public void hiddenFieldShouldNotBePresent() {
            hiddenField.shouldNotBePresent();
        }

        public void fieldDoesNotExistShouldContainText(String value) {
            fieldDoesNotExist.shouldContainText(value);
        }

        public void waitForFirstNameField() {
            waitForCondition().until(firstAndLastNameAreEnabled());
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
