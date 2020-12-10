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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
        assertThat(JUnitAdapter.isATaggableClass(Junit5Test.class)).isFalse();
        assertThat(JUnitAdapter.isATaggableClass(SerenityJunit5Test.class)).isTrue();
        assertThat(JUnitAdapter
                .isAssumptionViolatedException(new org.opentest4j.TestAbortedException("Assumption violated!")))
                .isTrue();
        assertThat(JUnitAdapter.isIgnored(Junit5Test.class.getDeclaredMethod("shouldBeIgnored"))).isTrue();
    }

    @Test
    public void shouldDetectJunit5MetaAnnotations() throws NoSuchMethodException {
        assertThat(JUnitAdapter.isTestClass(Junit5MetaAnnotationTest.class)).isTrue();
        assertThat(JUnitAdapter.isTestMethod(Junit5MetaAnnotationTest.class.getDeclaredMethod("shouldSucceed"))).isTrue();
        assertThat(JUnitAdapter.isTestSetupMethod(Junit5MetaAnnotationTest.class.getDeclaredMethod("beforeAll"))).isTrue();
        assertThat(JUnitAdapter.isTestSetupMethod(Junit5MetaAnnotationTest.class.getDeclaredMethod("beforeEach"))).isTrue();
        assertThat(JUnitAdapter.isSerenityTestCase(Junit5MetaAnnotationTest.class)).isTrue();
        assertThat(JUnitAdapter.isATaggableClass(Junit5MetaAnnotationTest.class)).isTrue();
        assertThat(JUnitAdapter.isIgnored(Junit5MetaAnnotationTest.class.getDeclaredMethod("shouldBeIgnored"))).isTrue();
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

    @ExtendWith(SomeExtension.class)
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

    @ExtendWith(SerenityDummyExtension.class)
    static class SerenityJunit5Test {

        @org.junit.jupiter.api.Test
        void shouldSucceed() {

        }

    }

    @HigherLevelSerenityTestAnnotation
    static class Junit5MetaAnnotationTest {

        @UnrealisticMetaMetaAnnotation
        void beforeAll() {

        }

        @UnrealisticMetaMetaAnnotation
        void beforeEach() {

        }

        @UnrealisticMetaMetaAnnotation
        void shouldSucceed() {

        }

        @UnrealisticMetaMetaAnnotation
        void shouldBeIgnored() {
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

    public static class SerenityDummyExtension implements Extension, Taggable {

    }

    public static class SomeExtension implements Extension {

    }

    @Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @org.junit.jupiter.api.Test
    @BeforeEach
    @BeforeAll
    @Disabled
    public @interface UnrealisticMetaAnnotation {

    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @UnrealisticMetaAnnotation
    public @interface UnrealisticMetaMetaAnnotation {

    }

}
