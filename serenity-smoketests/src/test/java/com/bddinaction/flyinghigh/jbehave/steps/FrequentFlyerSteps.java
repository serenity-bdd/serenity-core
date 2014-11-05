package com.bddinaction.flyinghigh.jbehave.steps;

import com.bddinaction.flyinghigh.jbehave.steps.persona.FrequentFlyerPersona;
import com.bddinaction.flyinghigh.model.FrequentFlyer;
import com.bddinaction.flyinghigh.model.Status;
import net.thucydides.core.Thucydides;
import org.jbehave.core.annotations.Given;

public class FrequentFlyerSteps {
    @Given("$persona is a Frequent Flyer member")
    public void Joe_is_a_Frequent_Flyer_member(FrequentFlyerPersona persona) throws Throwable {
        Thucydides.getCurrentSession().put("member", persona.getFrequentFlyer());
    }

    @Given("I am a $status Frequent Flyer member")
    public void i_am_a_member_of_status(Status status) throws Throwable {
        FrequentFlyer member = FrequentFlyer.withFrequentFlyerNumber("12345678").named("Joe","Blogs").withStatus(status);
        Thucydides.getCurrentSession().put("member", member);
    }
}
