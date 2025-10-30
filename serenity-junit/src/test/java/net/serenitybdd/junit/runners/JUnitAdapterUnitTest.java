package net.serenitybdd.junit.runners;

import net.thucydides.model.adapters.TestFramework;
import net.thucydides.model.tags.Taggable;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import static org.assertj.core.api.Assertions.assertThat;

public class JUnitAdapterUnitTest {

    @Test
    public void shouldDetectJunit4ClassesCorrectly() throws NoSuchMethodException {
        assertThat(TestFramework.support().isTestClass(Junit4Test.class)).isTrue();
        assertThat(TestFramework.support().isTestMethod(Junit4Test.class.getMethod("shouldSucceed"))).isTrue();
        assertThat(TestFramework.support().isTestSetupMethod(Junit4Test.class.getMethod("beforeClass"))).isTrue();
        assertThat(TestFramework.support().isTestSetupMethod(Junit4Test.class.getMethod("before"))).isTrue();
        assertThat(TestFramework.support().isATaggableClass(Junit4Test.class)).isFalse();
        assertThat(TestFramework.support().isSerenityTestCase(Junit4Test.class)).isFalse();
        assertThat(TestFramework.support()
                .isAssumptionViolatedException(new org.junit.AssumptionViolatedException("Assumption violated!")))
                .isTrue();
        assertThat(TestFramework.support().isIgnored(Junit4Test.class.getMethod("shouldBeIgnored"))).isTrue();
    }

    public static class NoTestAtAll {

        public void justAMethod() {

        }

    }

    public static class Junit4Test {

        @BeforeClass
        public static void beforeClass() {
        }

        @Before
        public void before() {
        }

        @Test
        public void shouldSucceed() {
        }

        @Test
        @Ignore
        public void shouldBeIgnored() {
        }

    }

    @RunWith(InternalRunner.class)
    public static class TaggableJunit4Test {
        @Test
        public void shouldSucceed() {
        }
    }

    @RunWith(SerenityRunner.class)
    public static class SerenityJunit4Test {
        @Test
        public void shouldSucceed() {
        }
    }

    public static class InternalRunner extends BlockJUnit4ClassRunner implements Taggable {

        public InternalRunner(Class<?> clazz) throws InitializationError {
            super(clazz);
        }
    }

    public static class SerenityRunner extends BlockJUnit4ClassRunner implements Taggable {

        public SerenityRunner(Class<?> clazz) throws InitializationError {
            super(clazz);
        }
    }
}
