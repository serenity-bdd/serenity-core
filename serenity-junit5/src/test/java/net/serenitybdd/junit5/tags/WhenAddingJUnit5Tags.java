package net.serenitybdd.junit5.tags;

import net.serenitybdd.junit5.JUnit5Tags;
import net.thucydides.core.annotations.SingleBrowser;
import net.thucydides.model.domain.TestTag;
import org.junit.jupiter.api.*;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("When adding JUnit 5 tags to a Serenity JUnit 5 test")
class WhenAddingJUnit5Tags {

    @Nested
    @DisplayName("Adding tags to a method")
    class MethodLevelTags {

        @Tag("classleveltag")
        class ATestCaseWithTagsOnTheMethods {
            @Tag("methodleveltag")
            public void some_test_method() {
            }

            @Tags({@Tag("methodleveltag1"), @Tag("methodleveltag2")})
            public void some_test_method_with_many_tags() {
            }
        }

        @DisplayName("Using the @Tag annotation")
        @Test
        void singleTag() throws NoSuchMethodException {
            Method testMethod = ATestCaseWithTagsOnTheMethods.class.getMethod("some_test_method");

            assertThat(JUnit5Tags.forMethod(testMethod)).contains(TestTag.withValue("methodleveltag"));
        }

        @DisplayName("Using the @Tags annotation")
        @Test
        void multipleTag() throws NoSuchMethodException {
            Method testMethod = ATestCaseWithTagsOnTheMethods.class.getMethod("some_test_method_with_many_tags");

            assertThat(JUnit5Tags.forMethod(testMethod)).contains(TestTag.withValue("methodleveltag1"), TestTag.withValue("methodleveltag2"));
        }
    }

    @Nested
    @DisplayName("Adding tags to a class")
    class ClassLevelTags {

        @Tag("classleveltag")
        class SomeTestCaseWithAClassLevelTag {
            public void some_test_method() {
            }
        }

        @Tags({@Tag("classleveltag1"), @Tag("classleveltag2")})
        class SomeTestCaseWithSeveralClassLevelTags {
            public void some_test_method() {
            }
        }

        @DisplayName("Using the @Tag annotation")
        @Test
        void singleTag() throws NoSuchMethodException {
            Method testMethod = SomeTestCaseWithAClassLevelTag.class.getMethod("some_test_method");

            assertThat(JUnit5Tags.forMethod(testMethod)).contains(TestTag.withValue("classleveltag"));
        }

        @DisplayName("Using the @Tags annotation")
        @Test
        void multipleTag() throws NoSuchMethodException {
            Method testMethod = SomeTestCaseWithSeveralClassLevelTags.class.getMethod("some_test_method");

            assertThat(JUnit5Tags.forMethod(testMethod)).contains(TestTag.withValue("classleveltag1"), TestTag.withValue("classleveltag2"));
        }
    }

    @Nested
    @DisplayName("Adding tags to a method at the method and class level")
    class MethodAndClassLevelTags {

        @Tag("classleveltag")
        class ATestCaseWithTagsOnTheMethodsAndClass {
            @Tag("methodleveltag")
            public void some_test_method() {
            }

            @Tags({@Tag("methodleveltag1"), @Tag("methodleveltag2")})
            public void some_test_method_with_many_tags() {
            }
        }

        @DisplayName("Using the @Tag annotation")
        @Test
        void singleTag() throws NoSuchMethodException {
            Method testMethod = ATestCaseWithTagsOnTheMethodsAndClass.class.getMethod("some_test_method");

            assertThat(JUnit5Tags.forMethod(testMethod))
                    .contains(TestTag.withValue("methodleveltag"), TestTag.withValue("classleveltag"));
        }

        @DisplayName("Using the @Tags annotation")
        @Test
        void multipleTag() throws NoSuchMethodException {
            Method testMethod = ATestCaseWithTagsOnTheMethodsAndClass.class.getMethod("some_test_method_with_many_tags");

            assertThat(JUnit5Tags.forMethod(testMethod))
                    .contains(TestTag.withValue("methodleveltag1"),
                            TestTag.withValue("methodleveltag2"),
                            TestTag.withValue("classleveltag"));
        }
    }

    @Nested
    @DisplayName("Adding tags to a nested class")
    class NestedClassLevelTags {

        @Tag("classleveltag")
        class SomeTestCaseWithAClassLevelTag {

            @Tag("nestedclassleveltag")
            class NestedTestCase {

                @Tag("methodleveltag")
                public void some_test_method() {
                }
            }
        }

        @DisplayName("Using the @Tag annotation")
        @Test
        void singleTag() throws NoSuchMethodException {
            Method testMethod = SomeTestCaseWithAClassLevelTag.NestedTestCase.class.getMethod("some_test_method");

            assertThat(JUnit5Tags.forMethod(testMethod)).contains(
                    TestTag.withValue("methodleveltag"),
                    TestTag.withValue("nestedclassleveltag"),
                    TestTag.withValue("classleveltag")
            );
        }
    }

    @Nested
    @DisplayName("Adding the @singlebrowser tag using the @SingleBrowser annotation")
    class UsingTheSingleBrowserAnnotation {


        class ATestCaseWithSingleBrowserOnAMethod {
            @SingleBrowser
            public void some_test_method() {
            }
        }

        @SingleBrowser
        class ATestCaseWithSingleBrowserOnAClass {
            public void some_test_method() {
            }
        }

        @DisplayName("Using the @SingleBrowser annotation on a method")
        @Test
        void onAMethod() throws NoSuchMethodException {
            Method testMethod = ATestCaseWithSingleBrowserOnAMethod.class.getMethod("some_test_method");

            assertThat(JUnit5Tags.forMethod(testMethod)).contains(TestTag.withValue("singlebrowser"));
        }


        @DisplayName("Using the @SingleBrowser annotation on a class")
        @Test
        void onAClass() throws NoSuchMethodException {
            Method testMethod = ATestCaseWithSingleBrowserOnAClass.class.getMethod("some_test_method");

            assertThat(JUnit5Tags.forMethod(testMethod)).contains(TestTag.withValue("singlebrowser"));
        }
    }
}
