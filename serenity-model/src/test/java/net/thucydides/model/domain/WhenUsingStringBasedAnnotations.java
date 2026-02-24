package net.thucydides.model.domain;

import net.serenitybdd.annotations.Epic;
import net.serenitybdd.annotations.EpicFeatureStoryAnnotations;
import net.serenitybdd.annotations.Feature;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WhenUsingStringBasedAnnotations {

    // --- Sample annotated classes for testing enclosing class inheritance ---

    @Feature("Product Catalog")
    static class OuterWithFeature {
        @net.serenitybdd.annotations.Story("Searching by keyword")
        static class NestedWithStory {}

        static class NestedWithoutAnnotations {}
    }

    @Epic("E-commerce")
    static class OuterWithEpic {
        @net.serenitybdd.annotations.Story("Adding to cart")
        static class NestedWithStory {}

        static class NestedWithoutAnnotations {}
    }

    @Epic("E-commerce")
    @Feature("Product Catalog")
    static class OuterWithEpicAndFeature {
        @net.serenitybdd.annotations.Story("Searching by keyword")
        static class NestedWithStory {}

        @Feature("Checkout")
        static class NestedWithFeature {}

        static class NestedWithoutAnnotations {}
    }

    @Feature("Product Catalog")
    static class OuterWithFeatureOnly {
    }

    // --- Tests for EpicFeatureStoryAnnotations.forClass() ---

    @Nested
    class ForClassTagCollection {

        @Test
        void nestedClassInheritsFeatureTagFromEnclosingClass() {
            List<TestTag> tags = EpicFeatureStoryAnnotations.forClass(OuterWithFeature.NestedWithStory.class);

            assertThat(tags).contains(
                    TestTag.withName("Product Catalog").andType("feature"),
                    TestTag.withName("Searching by keyword").andType("story")
            );
        }

        @Test
        void nestedClassInheritsEpicTagFromEnclosingClass() {
            List<TestTag> tags = EpicFeatureStoryAnnotations.forClass(OuterWithEpic.NestedWithStory.class);

            assertThat(tags).contains(
                    TestTag.withName("E-commerce").andType("epic"),
                    TestTag.withName("Adding to cart").andType("story")
            );
        }

        @Test
        void nestedClassInheritsBothEpicAndFeatureTagsFromEnclosingClass() {
            List<TestTag> tags = EpicFeatureStoryAnnotations.forClass(OuterWithEpicAndFeature.NestedWithStory.class);

            assertThat(tags).contains(
                    TestTag.withName("E-commerce").andType("epic"),
                    TestTag.withName("Product Catalog").andType("feature"),
                    TestTag.withName("Searching by keyword").andType("story")
            );
        }

        @Test
        void nestedClassWithNoAnnotationsStillInheritsFromEnclosingClass() {
            List<TestTag> tags = EpicFeatureStoryAnnotations.forClass(OuterWithFeature.NestedWithoutAnnotations.class);

            assertThat(tags).contains(
                    TestTag.withName("Product Catalog").andType("feature")
            );
        }

        @Test
        void nonNestedClassStillWorksAsExpected() {
            List<TestTag> tags = EpicFeatureStoryAnnotations.forClass(OuterWithFeatureOnly.class);

            assertThat(tags).containsExactly(
                    TestTag.withName("Product Catalog").andType("feature")
            );
        }
    }

    // --- Tests for Story.fromAnnotationsOn() ---

    @Nested
    class FromAnnotationsOnHierarchy {

        @Test
        void nestedClassInheritsFeatureFromEnclosingClassInStoryPath() {
            Story story = Story.fromAnnotationsOn(OuterWithFeature.NestedWithStory.class);

            assertThat(story).isNotNull();
            assertThat(story.getPath()).isEqualTo("Product Catalog/Searching by keyword");
        }

        @Test
        void nestedClassInheritsEpicFromEnclosingClassInStoryPath() {
            Story story = Story.fromAnnotationsOn(OuterWithEpic.NestedWithStory.class);

            assertThat(story).isNotNull();
            assertThat(story.getPath()).isEqualTo("E-commerce/Adding to cart");
        }

        @Test
        void fullHierarchyAcrossOuterAndNestedClasses() {
            Story story = Story.fromAnnotationsOn(OuterWithEpicAndFeature.NestedWithStory.class);

            assertThat(story).isNotNull();
            assertThat(story.getPath()).isEqualTo("E-commerce/Product Catalog/Searching by keyword");
        }

        @Test
        void nestedClassAnnotationOverridesEnclosingClassAnnotationAtSameLevel() {
            Story story = Story.fromAnnotationsOn(OuterWithEpicAndFeature.NestedWithFeature.class);

            assertThat(story).isNotNull();
            // Nested class has @Feature("Checkout") which should take precedence over outer's @Feature("Product Catalog")
            assertThat(story.getPath()).isEqualTo("E-commerce/Checkout");
        }

        @Test
        void nestedClassWithNoAnnotationsInheritsFromEnclosingClass() {
            Story story = Story.fromAnnotationsOn(OuterWithFeature.NestedWithoutAnnotations.class);

            assertThat(story).isNotNull();
            assertThat(story.getPath()).isEqualTo("Product Catalog");
        }

        @Test
        void nestedClassWithDefaultStoryNameUsesItUnderFeature() {
            Story story = Story.fromAnnotationsOn(OuterWithFeature.NestedWithoutAnnotations.class, "My Display Name");

            assertThat(story).isNotNull();
            assertThat(story.getPath()).isEqualTo("Product Catalog/My Display Name");
        }
    }
}
