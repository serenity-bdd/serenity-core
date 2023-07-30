package net.serenitybdd.junit.spring;

import net.serenitybdd.junit.spring.integration.SpringIntegrationClassRule;
import net.serenitybdd.junit.spring.integration.SpringIntegrationMethodRule;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@RunWith(SpringIntegrationSerenityRunner.class)
@ContextConfiguration("classpath:/spring/integration-rules-context.xml")
public class WhenSerenityRunnerTestPlaysWellWithExistingRules {

    @Autowired
    public StringHolder stringHolder;

    @Rule
    public SpringIntegrationMethodRule springIntegrationMethodRule = new SpringIntegrationMethodRule();

    @Rule
    public MethodRule doNothingMethodRule = (base, method, target) -> base;

    @ClassRule
    public static TestRule doNothingClassRule = (base, description) -> base;

    @ClassRule
    public static SpringIntegrationClassRule springIntegrationClassRule = new SpringIntegrationClassRule();

    @Test
    public void testIfSpringClassRuleIncludedByRunnerByCheckingStack() {
        StackChecker.checkForClassRule();
    }

    @Test
    public void testIfSpringMethodRuleIncludedByRunnerByCheckingStack() {
        StackChecker.checkForMethodRule();
    }

    @Test
    public void testIfObjectPreparedBySpringByTestingIfInjectionWorks() {
        Assertions.assertThat(this.stringHolder).isNotNull();
    }
}
