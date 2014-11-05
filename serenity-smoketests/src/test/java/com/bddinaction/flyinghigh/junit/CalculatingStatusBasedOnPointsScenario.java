package com.bddinaction.flyinghigh.junit;

import com.bddinaction.flyinghigh.model.FrequentFlyer;
import com.bddinaction.flyinghigh.model.Status;
import net.thucydides.junit.runners.ThucydidesRunner;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Step;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(ThucydidesRunner.class)
public class CalculatingStatusBasedOnPointsScenario {

    @Steps
    private EarningStatusPointsSteps steps;

    @Test
    public void new_members_should_start_out_as_Bronze() {
        steps.user_is_not_a_Frequent_Flyer_member("Jill", "Smith");
        steps.registers_on_the_Frequent_Flyer_program();
        steps.should_have_status_of(Status.Bronze);
    }

    public class EarningStatusPointsSteps {
        String firstName;
        String lastName;
        FrequentFlyer member;

        @Step("Given {0} {1} is not a Frequent Flyer member")
        public void user_is_not_a_Frequent_Flyer_member(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Step("When she registers on the Frequent Flyer program")
        public void registers_on_the_Frequent_Flyer_program() {
            member = FrequentFlyer.withFrequentFlyerNumber("123456789")
                    .named(firstName, lastName);
        }

        @Step("Then she should have a status of {0}")
        public void should_have_status_of(Status finalStatus) {
            assertThat(member.getStatus()).isEqualTo(finalStatus);
        }
    }


}
