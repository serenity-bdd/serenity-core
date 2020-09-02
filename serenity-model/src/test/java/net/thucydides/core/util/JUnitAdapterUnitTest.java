package net.thucydides.core.util;

import net.thucydides.core.tags.Taggable;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.JUnit4;
import org.junit.runners.model.InitializationError;

import java.lang.annotation.*;

import static org.assertj.core.api.Assertions.assertThat;

public class JUnitAdapterUnitTest {

    @Test
    public void shouldHandleNullInput() {
        assertThat(JUnitAdapter.isTestClass(null)).isFalse();
        assertThat(JUnitAdapter.isTestMethod(null)).isFalse();
        assertThat(JUnitAdapter.isTestSetupMethod(null)).isFalse();
        assertThat(JUnitAdapter.isATaggableClass(null)).isFalse();
        assertThat(JUnitAdapter.isSerenityTestCase(null)).isFalse();
        assertThat(JUnitAdapter.isIgnored(null)).isFalse();
    }

    @Test
    public void shouldNotDetectOtherClasses() throws NoSuchMethodException {
        assertThat(JUnitAdapter.isTestClass(NoTestAtAll.class)).isFalse();
        assertThat(JUnitAdapter.isTestMethod(NoTestAtAll.class.getMethod("justAMethod"))).isFalse();
        assertThat(JUnitAdapter.isTestSetupMethod(NoTestAtAll.class.getMethod("justAMethod"))).isFalse();
        assertThat(JUnitAdapter.isATaggableClass(NoTestAtAll.class)).isFalse();
        assertThat(JUnitAdapter.isSerenityTestCase(NoTestAtAll.class)).isFalse();
        assertThat(JUnitAdapter.isAssumptionViolatedException(new RuntimeException("Assumption violated!"))).isFalse();
        assertThat(JUnitAdapter.isIgnored(NoTestAtAll.class.getMethod("justAMethod"))).isFalse();
    }

    @Test
    public void shouldDetectJunit4ClassesCorrectly() throws NoSuchMethodException {
        assertThat(JUnitAdapter.isTestClass(Junit4Test.class)).isTrue();
        assertThat(JUnitAdapter.isTestMethod(Junit4Test.class.getMethod("shouldSucceed"))).isTrue();
        assertThat(JUnitAdapter.isTestSetupMethod(Junit4Test.class.getMethod("beforeClass"))).isTrue();
        assertThat(JUnitAdapter.isTestSetupMethod(Junit4Test.class.getMethod("before"))).isTrue();
        assertThat(JUnitAdapter.isATaggableClass(Junit4Test.class)).isFalse();
        assertThat(JUnitAdapter.isATaggableClass(TaggableJunit4Test.class)).isTrue();
        assertThat(JUnitAdapter.isSerenityTestCase(Junit4Test.class)).isFalse();
        assertThat(JUnitAdapter.isSerenityTestCase(SerenityJunit4Test.class)).isTrue();
        assertThat(JUnitAdapter
                .isAssumptionViolatedException(new org.junit.AssumptionViolatedException("Assumption violated!")))
                .isTrue();
        assertThat(JUnitAdapter.isIgnored(Junit4Test.class.getMethod("shouldBeIgnored"))).isTrue();
    }

    @Test
    public void shouldDetectJunit5ClassesCorrectly() throws NoSuchMethodException {
        assertThat(JUnitAdapter.isTestClass(Junit5Test.class)).isTrue();
        assertThat(JUnitAdapter.isTestMethod(Junit5Test.class.getDeclaredMethod("shouldSucceed"))).isTrue();
        assertThat(JUnitAdapter.isTestSetupMethod(Junit5Test.class.getDeclaredMethod("beforeAll"))).isTrue();
        assertThat(JUnitAdapter.isTestSetupMethod(Junit5Test.class.getDeclaredMethod("beforeEach"))).isTrue();
        assertThat(JUnitAdapter.isATaggableClass(Junit5Test.class)).isFalse();
        assertThat(JUnitAdapter.isSerenityTestCase(Junit5Test.class)).isFalse();
        assertThat(JUnitAdapter.isSerenityTestCase(SerenityJunit5Test.class)).isTrue();
        assertThat(JUnitAdapter
                .isAssumptionViolatedException(new org.opentest4j.TestAbortedException("Assumption violated!")))
                .isTrue();
        assertThat(JUnitAdapter.isIgnored(Junit5Test.class.getDeclaredMethod("shouldBeIgnored"))).isTrue();
    }

    public static class NoTestAtAll {

        public void justAMethod() {

        }

    }

    @RunWith(JUnit4.class)
    public static class Junit4Test {

        @BeforeClass
        public void beforeClass() {
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

    }

    @RunWith(SerenityRunner.class)
    public static class SerenityJunit4Test {

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
        void beforeAll() {

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
