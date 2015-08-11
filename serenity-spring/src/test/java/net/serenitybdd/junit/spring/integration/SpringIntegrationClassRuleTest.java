package net.serenitybdd.junit.spring.integration;

import javax.inject.Inject;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.junit.spring.integration.testutils.StringHolder;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    SpringIntegrationClassRuleTest.Class1WithoutDirtyContextShouldSucceed.class,
    SpringIntegrationClassRuleTest.Class2WithDirtyContextWithoutClassRuleShouldFail.class,
    SpringIntegrationClassRuleTest.Class3WithoutDirtyContextShouldFail.class,
    SpringIntegrationClassRuleTest.Class4WithDirtyContext.class,
    SpringIntegrationClassRuleTest.Class5CheckDirtyContextWorked.class
})
public class SpringIntegrationClassRuleTest {
    private static void doTest(StringHolder holder, boolean expectedAlreadyModified) {
        if (!expectedAlreadyModified) {
            Assertions
                .assertThat(holder.getValue())
                .isEqualTo("not-modified");
            holder.setValue("modified");
        }
        Assertions
            .assertThat(holder.getValue())
            .isEqualTo("modified");
    }

    @RunWith(SerenityRunner.class)
    @ContextConfiguration("classpath:/spring/integration-rules-context.xml")
    public static abstract class Base {
        @Before
        public void before() throws Exception {
            // Inject.
            new TestContextManager(this.getClass()).prepareTestInstance(this);
        }

        @Inject
        public StringHolder instance;
    }

    public static class Class1WithoutDirtyContextShouldSucceed extends Base {
        @ClassRule
        public static SpringIntegrationClassRule classRule = new SpringIntegrationClassRule();

        @Test
        public void testStringHolderShouldInitiallyBeUnmodified() {
            SpringIntegrationClassRuleTest.doTest(instance, false);
        }
    }

    @DirtiesContext(classMode = ClassMode.AFTER_CLASS)
    public static class Class2WithDirtyContextWithoutClassRuleShouldFail extends Base {
        @Test
        public void testStringHolderShouldBeModified() {
            SpringIntegrationClassRuleTest.doTest(instance, true);
        }
    }

    public static class Class3WithoutDirtyContextShouldFail extends Base {
        @ClassRule
        public static SpringIntegrationClassRule classRule = new SpringIntegrationClassRule();

        @Test
        public void testStringHolderShouldStillBeModified() {
            SpringIntegrationClassRuleTest.doTest(instance, true);
        }
    }

    @DirtiesContext(classMode = ClassMode.AFTER_CLASS)
    public static class Class4WithDirtyContext extends Base {
        @ClassRule
        public static SpringIntegrationClassRule classRule = new SpringIntegrationClassRule();

        @Test
        public void testContextHopefullyResets() {
            instance.setValue("modified again");
        }
    }

    @DirtiesContext(classMode = ClassMode.AFTER_CLASS)
    public static class Class5CheckDirtyContextWorked extends Base {
        @ClassRule
        public static SpringIntegrationClassRule classRule = new SpringIntegrationClassRule();

        @Test
        public void testStringHolderShouldHaveBeenResetToUnmodified() {
            SpringIntegrationClassRuleTest.doTest(instance, false);
        }
    }
}
