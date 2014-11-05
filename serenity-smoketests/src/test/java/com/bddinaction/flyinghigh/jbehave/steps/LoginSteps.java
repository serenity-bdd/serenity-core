package com.bddinaction.flyinghigh.jbehave.steps;


import com.bddinaction.flyinghigh.jbehave.flowsteps.TravellerFlowSteps;
import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import java.io.IOException;

/**
 * A description goes here.
 * User: john
 * Date: 4/02/2014
 * Time: 10:35 PM
 */
public class LoginSteps {

    @Steps
    TravellerFlowSteps traveller;

    @Given("$username has registered online with a password of '$password'")
    public void registered_online_with_a_password_of_secret(String username, String password) throws Throwable {
    }

    @When("$username logs on with password '$password'")
    public void Joe_logs_on_with_password_secret(String username, String password) {
    }

    @Then("he should be given access to the site")
    public void he_should_be_given_access_to_the_site() {
    }


    @Then("he should be informed that his password was incorrect")
    public void he_should_be_informed_that_his_password_was_incorrect() {
        //traveller.shouldSeeErrorMessage("expected message");
    }

    @Given("the account has expired")
    public void the_account_has_expired() throws Throwable {
        //throw new IOException("Database crashed");
    }

    @Then("he should be informed that his account has expired")
    public void he_should_be_informed_that_his_account_has_expired() {
    }

    @Then("he should be invited to renew his account")
    public void he_should_be_invited_to_renew_his_account() {
    }

    @Given("$username has registered online via Facebook")
    public void has_registered_via_facebook(String username) {
    }

    @When("$username logs on with a Facebook token")
    public void logs_in_via_facebook(String username) {}

    @Given("$username has registered online via GMail")
    public void has_registered_via_gmail(String username) {
    }

    @When("$username logs on with a GMail token")
    public void logs_in_via_gmail(String username) {}

}
