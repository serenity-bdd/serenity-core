package net.serenitybdd.junit.spring;

import net.serenitybdd.annotations.Title;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.junit.spring.integration.SpringIntegrationClassRule;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

@RunWith(SerenityRunner.class)
@ContextConfiguration("classpath:/spring/integration-rules-context.xml")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class WhenNeitherRuleNorDirtiesContextPresent {

    @Autowired
    public StringHolder stringHolder;

    @ClassRule
    public static SpringIntegrationClassRule classRule = new SpringIntegrationClassRule();

    @Rule


    public PassFailureCountingRule testRule = new PassFailureCountingRule(1,1);

    @Before
    public void before() throws Exception {
        // Inject.
        new TestContextManager(this.getClass()).prepareTestInstance(this);
    }

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

    @Title("Test #2: One of these methods should pass, the other should fail. Using PassFailureCountingRule, if this is the case, this test class will pass, otherwise one of the methods will have it's failure result replaced.")
    @Test
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
