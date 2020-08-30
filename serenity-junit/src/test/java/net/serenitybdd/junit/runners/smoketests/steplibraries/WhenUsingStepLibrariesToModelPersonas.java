package net.serenitybdd.junit.runners.smoketests.steplibraries;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebDriver;

import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SerenityRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
/**
 * This is an example of how step libraries can be used to model persona behaviour in tests
 */
public class WhenUsingStepLibrariesToModelPersonas {

    @Managed(driver = "chrome", options = "--headless")
    WebDriver driver;

    public static class AccountApplicationPage extends PageObject {
        public void enterCustomerName(String firstName, String lastName) {}
        public void enterDateOfBirth() {}
        public void enterAddress() {}
        public void apply() {}
    }

    public static class AccountHolder {

        private long newBankAccountNumber = 0;
        public String firstName;
        public String lastName;

        @Step("Given an account holder called {0} {1}")
        public void isCalled(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        AccountApplicationPage accountApplicationPage;

        /**
         * A client opens a new bank account via the client website.
         * We record the bank account number for future use
         */
        @Step
        public void choosesToOpenABankAccount() {
            newBankAccountNumber = new Random().nextLong();
        }

        /**
         * Does this client have an open account?
         */
        public boolean hasAnOpenAccount() { return newBankAccountNumber != 0; }
        public long newBankAccountNumber() { return newBankAccountNumber; }

        @Step
        public void providesHerPersonalDetails() {
            accountApplicationPage.enterCustomerName(firstName, lastName);
            accountApplicationPage.enterDateOfBirth();
            accountApplicationPage.enterAddress();
        }

        @Step
        public void appliesForACurrentAccount() {
            accountApplicationPage.apply();
        }
    }

    @Steps
    AccountHolder joe;

    @Steps
    AccountHolder jane;

    @Test
    public void jane_opens_an_account() {
        jane.isCalled("Jane","Smith");

        jane.choosesToOpenABankAccount();
        jane.providesHerPersonalDetails();
        jane.appliesForACurrentAccount();

        assertThat(jane.hasAnOpenAccount(), is(true));
        assertThat(joe.hasAnOpenAccount(), is(false));
    }

    /**
     * Both step libraries are reset at the start of the test
     */
    @Test
    public void joe_opens_an_account() {

        joe.choosesToOpenABankAccount();

        assertThat(joe.hasAnOpenAccount(), is(true));
        assertThat(jane.hasAnOpenAccount(), is(false));
    }

}
