package net.serenitybdd.junit.spring;

import javax.inject.Inject;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

@RunWith(SpringIntegrationSerenityRunner.class)
@ContextConfiguration("classpath:/spring/integration-rules-context.xml")
public class WhenIncludingSpringIntegrationSerenityRunnerRules {
    @Inject
    public StringHolder stringHolder;

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
