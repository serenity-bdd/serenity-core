package net.serenitybdd.junit.spring.integration;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.junit.spring.SpringIntegration;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.TestContextManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A serenity runner that automatically adds rules {@link SpringIntegrationMethodRule} and {@link SpringIntegrationClassRule}.
 * @author scott.dennison@costcutter.com
 */
public class SpringIntegrationSerenityRunner extends SerenityRunner {
    private final TestContextManager testContextManager;

    /**
     * Create a Serenity runner for a particular class.
     * @param testClass The class to test.
     * @throws InitializationError If the super constructor throws an {@link org.junit.runners.model.InitializationError}.
     */
    public SpringIntegrationSerenityRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        this.testContextManager = SpringIntegrationRuleBase.createTestContextManager(testClass);
    }

    /**
     * Prepares the test instance after creating the test instance.
     * @return The new test instance.
     * @throws Exception Passed up from the superclass.
     */
    @Override
    public Object createTest() throws Exception {
        Object instance = super.createTest();
        this.testContextManager.prepareTestInstance(instance);
        return instance;
    }

    /**
     * Sets up an instance of {@link SpringIntegrationMethodRule} in the list of method rules, creating one if missing. Also removes any {@link SpringIntegration} rule.
     *
     * @param target The target instance. Not used here, but passed down to super method.
     * @return The list of method rules.
     */
    @Override
    protected List<MethodRule> rules(Object target) {
        List<MethodRule> rules = new ArrayList<>();
        SpringIntegrationMethodRule siMethodRule = null;
        for (MethodRule rule : super.rules(target)) {
            if (!(rule instanceof SpringIntegration)) {
                rules.add(rule);
                if (rule instanceof SpringIntegrationMethodRule) {
                    siMethodRule = (SpringIntegrationMethodRule)rule;
                }
            }
        }
        if (siMethodRule == null) {
            siMethodRule = new SpringIntegrationMethodRule();
            rules.add(siMethodRule);
        }
        siMethodRule.setup(false,this.testContextManager);
        return rules;
    }

    /**
     * Sets up an instance of {@link SpringIntegrationClassRule} in the list of class rules, creating one if missing.
     * @return The list of class rules.
     */
    @Override
    protected List<TestRule> classRules() {
        List<TestRule> classRules = new ArrayList<>();
        SpringIntegrationClassRule siClassRule = null;
        for (TestRule rule : super.classRules()) {
            classRules.add(rule);
            if (rule instanceof SpringIntegrationClassRule) {
                siClassRule = (SpringIntegrationClassRule)rule;
            }
        }
        if (siClassRule == null) {
            siClassRule = new SpringIntegrationClassRule();
            classRules.add(siClassRule);
        }
        siClassRule.setup(false,this.testContextManager);
        return classRules;
    }
}
