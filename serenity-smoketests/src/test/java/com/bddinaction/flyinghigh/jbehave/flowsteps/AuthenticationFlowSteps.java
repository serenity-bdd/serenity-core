package com.bddinaction.flyinghigh.jbehave.flowsteps;

import com.bddinaction.flyinghigh.jbehave.model.FrequentFlyerMember;
import com.bddinaction.flyinghigh.jbehave.pages.HomePage;
import com.bddinaction.flyinghigh.jbehave.pages.LoginPage;
import net.thucydides.core.annotations.Step;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class AuthenticationFlowSteps {

    protected LoginPage loginPage;
    protected HomePage homePage;

    @Step
    public void enterEmailAndPasswordFor(FrequentFlyerMember user) {
        loginPage.open();
        loginPage.signinAs(user.getEmail(), user.getPassword());
    }

    @Step
    public void verifyWelcomeMessageFor(FrequentFlyerMember user) {
        String welcomeMessage = homePage.getWelcomeMessage();
        assertThat(welcomeMessage, is(equalTo("Welcome " + user.getFirstName())));
    }

    @Step
    public void shouldSeeErrorMessage(String expectedMessage) {
        assertThat("wrong message", is(equalTo(expectedMessage)));
    }
}
