package com.bddinaction.flyinghigh.jbehave.steps;

import com.bddinaction.flyinghigh.jbehave.flowsteps.AuthenticationFlowSteps;
import com.bddinaction.flyinghigh.jbehave.model.FrequentFlyerMember;
import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

public class UserAuthenticationSteps {

    @Steps
    private AuthenticationFlowSteps registeredMember;

    @Given("$user is a registered Frequent Flyer")
    public void givenARegisteredFrequentFlyer(FrequentFlyerMember user) {}

    @When("$user authenticates with a valid email address and password")
    public void whenJaneAuthenticatesWithAValidEmailAddressAndPassword(FrequentFlyerMember user) {
        registeredMember.enterEmailAndPasswordFor(user);
    }

    @Then("$user should be given access to her account")
    @Alias("$user should be given access to his account")
    public void thenTheUserShouldBeGivenAccessToAccount(FrequentFlyerMember user) {
        registeredMember.verifyWelcomeMessageFor(user);
    }

    @Given("$user has logged on")
    public void aUserHasLoggedOnAs(FrequentFlyerMember user) {
        registeredMember.enterEmailAndPasswordFor(user);
    }

    @When("$user views the home page")
    public void whenAUserViewsTheHomePage() {}

}
