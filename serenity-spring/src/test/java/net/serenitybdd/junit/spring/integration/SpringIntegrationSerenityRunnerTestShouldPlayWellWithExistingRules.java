package net.serenitybdd.junit.spring.integration;

import javax.inject.Inject;
import net.serenitybdd.junit.spring.integration.testutils.StackChecker;
import net.serenitybdd.junit.spring.integration.testutils.StringHolder;
import net.thucydides.junit.spring.SpringIntegration;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.test.context.ContextConfiguration;

@RunWith(SpringIntegrationSerenityRunner.class)
@ContextConfiguration("classpath:/spring/integration-rules-context.xml")
public class SpringIntegrationSerenityRunnerTestShouldPlayWellWithExistingRules {
    @Inject
    public StringHolder stringHolder;

    @Rule
    public SpringIntegration springIntegration = new SpringIntegration();

    @Rule
    public SpringIntegrationMethodRule springIntegrationMethodRule = new SpringIntegrationMethodRule();

    @Rule
    public MethodRule doNothingMethodRule = new MethodRule() { @Override public Statement apply(Statement base, FrameworkMethod method, Object target) { return base; } };

    @ClassRule
    public static TestRule doNothingClassRule = new TestRule() { @Override public Statement apply(Statement base, Description description) { return base; } };

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
