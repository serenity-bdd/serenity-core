package net.serenitybdd.junit.spring;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.junit.spring.integration.SpringIntegrationClassRule;
import net.serenitybdd.junit.spring.integration.SpringIntegrationMethodRule;
import net.serenitybdd.annotations.Title;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;

@RunWith(SerenityRunner.class)
@ContextConfiguration("classpath:/spring/integration-rules-context.xml")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class WhenOnlyRulePresent {

    @Autowired
    public StringHolder stringHolder;

    @Rule
    public SpringIntegrationMethodRule methodRule = new SpringIntegrationMethodRule();

    @ClassRule
    public static SpringIntegrationClassRule classRule = new SpringIntegrationClassRule();

    @Rule
    public PassFailureCountingRule testRule = new PassFailureCountingRule(1,1);

    @Test
    @Title("Test #1: One of these methods should pass, the other should fail. Using PassFailureCountingRule, if this is the case, this test class will pass, otherwise one of the methods will have it's failure result replaced.")
    public void modifyStringHolderTest1() {
        Assertions
            .assertThat(this.stringHolder.getValue())
            .isEqualTo("not-modified");
        this.stringHolder.setValue("modified");
        Assertions
            .assertThat(this.stringHolder.getValue())
            .isEqualTo("modified");
    }

    @Test
    @Title("Test #2: One of these methods should pass, the other should fail. Using PassFailureCountingRule, if this is the case, this test class will pass, otherwise one of the methods will have it's failure result replaced.")
    public void modifyStringHolderTest2() {
        Assertions
            .assertThat(this.stringHolder.getValue())
            .isEqualTo("not-modified");
        this.stringHolder.setValue("modified");
        Assertions
            .assertThat(this.stringHolder.getValue())
            .isEqualTo("modified");
    }
}
