package com.bddinaction.flyinghigh.jbehave.steps;

import com.bddinaction.flyinghigh.model.FrequentFlyer;
import com.bddinaction.flyinghigh.model.Status;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import static org.fest.assertions.api.Assertions.assertThat;

public class EarningStatus {

    String firstName;
    String lastName;

    @Given("$firstName $lastName is not a Frequent Flyer member")
    public void not_a_Frequent_Flyer_member(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    FrequentFlyer member;

    @Given("$firstName $lastName is a $status Frequent Flyer member")
    public void a_Frequent_Flyer_member(String firstName, String lastName, Status status) {
        member = FrequentFlyer.withFrequentFlyerNumber("12345678")
                              .named(firstName,lastName);
        member.setStatus(status);
    }

    @Given("a member has a status of <initialStatus>")
    @Alias("a member has a status of <status>")
    public void a_Frequent_Flyer_member(Status initialStatus) {
        member = FrequentFlyer.withFrequentFlyerNumber("12345678").named("Joe","Bloggs");
        member.setStatus(initialStatus);
    }

    @When("he registers on the Frequent Flyer program")
    @Alias("she registers on the Frequent Flyer program")
    public void registers_on_the_Frequent_Flyer_program() throws Throwable {
        member = FrequentFlyer.withFrequentFlyerNumber("123456789")
                              .named(firstName, lastName);
    }

    @Given("he has <initialStatusPoints> status points")
    public void earned_status_points(int initialStatusPoints) {
        member.setStatusPoints(initialStatusPoints);
    }

    @When("he earns <extraPoints> extra status points")
    public void earn_extra_status_points(int extraPoints) {
        member.earns(extraPoints).statusPoints();
    }


    @Then("he should have a status of <finalStatus>")
    @Alias("she should have a status of $finalStatus")
    public void should_have_status_of(Status finalStatus) {
        assertThat(member.getStatus()).isEqualTo(finalStatus);
    }
}
