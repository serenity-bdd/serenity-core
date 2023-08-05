package net.serenitybdd.junit5;

import net.thucydides.model.adapters.TestFramework;
import net.thucydides.model.tags.Taggable;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.JUnit4;
import org.junit.runners.model.InitializationError;

import java.lang.annotation.*;

import static org.assertj.core.api.Assertions.assertThat;

public class JUnitAdapterUnitTest {

    @Test
    public void shouldHandleNullInput() {
        assertThat(TestFramework.support().isTestClass(null)).isFalse();
        assertThat(TestFramework.support().isTestMethod(null)).isFalse();
        assertThat(TestFramework.support().isTestSetupMethod(null)).isFalse();
        assertThat(TestFramework.support().isATaggableClass(null)).isFalse();
        assertThat(TestFramework.support().isSerenityTestCase(null)).isFalse();
        assertThat(TestFramework.support().isIgnored(null)).isFalse();
    }

    @Test
    public void shouldNotDetectOtherClasses() throws NoSuchMethodException {
        assertThat(TestFramework.support().isTestClass(NoTestAtAll.class)).isFalse();
        assertThat(TestFramework.support().isTestMethod(NoTestAtAll.class.getMethod("justAMethod"))).isFalse();
        assertThat(TestFramework.support().isTestSetupMethod(NoTestAtAll.class.getMethod("justAMethod"))).isFalse();
        assertThat(TestFramework.support().isATaggableClass(NoTestAtAll.class)).isFalse();
        assertThat(TestFramework.support().isSerenityTestCase(NoTestAtAll.class)).isFalse();
        assertThat(TestFramework.support().isAssumptionViolatedException(new RuntimeException("Assumption violated!"))).isFalse();
        assertThat(TestFramework.support().isIgnored(NoTestAtAll.class.getMethod("justAMethod"))).isFalse();
    }

    @Test
    public void shouldDetectJunit5ClassesCorrectly() throws NoSuchMethodException {
        assertThat(TestFramework.support().isTestClass(Junit5Test.class)).isTrue();
        assertThat(TestFramework.support().isTestMethod(Junit5Test.class.getDeclaredMethod("shouldSucceed"))).isTrue();
        assertThat(TestFramework.support().isTestSetupMethod(Junit5Test.class.getDeclaredMethod("beforeAll"))).isTrue();
        assertThat(TestFramework.support().isTestSetupMethod(Junit5Test.class.getDeclaredMethod("beforeEach"))).isTrue();
        assertThat(TestFramework.support().isATaggableClass(Junit5Test.class)).isFalse();
        assertThat(TestFramework.support().isSerenityTestCase(Junit5Test.class)).isTrue();
        assertThat(TestFramework.support().isSerenityTestCase(Junit5ParameterizedTest.class)).isTrue();
        assertThat(TestFramework.support().isSerenityTestCase(SerenityJunit5Test.class)).isTrue();
        assertThat(TestFramework.support()
                .isAssumptionViolatedException(new org.opentest4j.TestAbortedException("Assumption violated!")))
                .isTrue();
        assertThat(TestFramework.support().isIgnored(Junit5Test.class.getDeclaredMethod("shouldBeIgnored"))).isTrue();
    }

    public static class NoTestAtAll {

        public void justAMethod() {

        }

    }

    @RunWith(JUnit4.class)
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

    static class Junit5Test {

        @BeforeAll
        static void beforeAll() {

        }

        @BeforeEach
        void beforeEach() {

        }

        @org.junit.jupiter.api.Test
        void shouldSucceed() {

        }

        @org.junit.jupiter.api.Test
        @Disabled
        void shouldBeIgnored() {
        }
    }

    static class Junit5ParameterizedTest {


        @ParameterizedTest
        void shouldSucceed() {

        }

    }

    @HigherLevelSerenityTestAnnotation
    static class SerenityJunit5Test {

        @org.junit.jupiter.api.Test
        void shouldSucceed() {

        }

    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @ExtendWith(SerenityDummyExtension.class)
    public @interface SerenityTestAnnotation {

    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @SerenityTestAnnotation
    public @interface HigherLevelSerenityTestAnnotation {

    }

    public static class SerenityDummyExtension implements Extension {

    }

}
