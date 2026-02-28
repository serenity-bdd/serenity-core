package net.thucydides.model.annotations

import net.serenitybdd.annotations.Feature
import net.serenitybdd.annotations.TestAnnotations
import net.serenitybdd.annotations.WithTag
import net.thucydides.model.domain.TestTag
import spock.lang.Specification

class WhenReadingTagsInAClass extends Specification {

    @WithTag("flavor:strawberry")
    static class StrawberryClass {
    }

    @WithTag("color:red")
    static class RedClass {
        @WithTag("temperature:warm")
        public void someMethod(){}
    }

    @WithTag("color:pink")
    static class StrawberryIcecream extends StrawberryClass {
        @WithTag("temperature:cold")
        public void someMethod(){}
    }

   def "should find all the class-level tags"() {
       when:
            def tags = TestAnnotations.forClass(RedClass).getClassTags()
       then:
            tags == [TestTag.withName("red").andType("color")]
   }

    def "should find all the method-level tags"() {
        when:
        def tags = TestAnnotations.forClass(RedClass).getTagsForMethod("someMethod")
        then:
        tags == [TestTag.withName("red").andType("color"), TestTag.withName("warm").andType("temperature")]
    }

    def "should find tags from parent classes"() {
        when:
        def tags = TestAnnotations.forClass(StrawberryIcecream).getClassTags()
        then:
        tags == [TestTag.withName("pink").andType("color"), TestTag.withName("strawberry").andType("flavor")]
    }

    def "should find all the method-level tags using inherited tags"() {
        when:
        def tags = TestAnnotations.forClass(StrawberryIcecream).getTagsForMethod("someMethod")
        println tags
        then:
        tags == [TestTag.withName("pink").andType("color"),  TestTag.withName("strawberry").andType("flavor"), TestTag.withName("cold").andType("temperature")]
    }

    @Feature("Shopping Cart")
    static class OuterTestClass {
        static class NestedStoryClass {
            public void someTest(){}
        }
    }

    @Feature("Product Catalog")
    @WithTag("priority:high")
    static class AnnotatedOuterClass {
        @WithTag("scope:smoke")
        static class AnnotatedNestedClass {
            public void someTest(){}
        }
    }

    def "should find tags from enclosing classes for nested inner classes"() {
        when:
        def tags = TestAnnotations.forClass(OuterTestClass.NestedStoryClass).getClassTags()
        then:
        tags.contains(TestTag.withName("Shopping Cart").andType("feature"))
    }

    def "should find both inner and outer class tags for nested classes"() {
        when:
        def tags = TestAnnotations.forClass(AnnotatedOuterClass.AnnotatedNestedClass).getClassTags()
        then:
        tags.contains(TestTag.withName("Product Catalog").andType("feature"))
        tags.contains(TestTag.withName("high").andType("priority"))
        tags.contains(TestTag.withName("smoke").andType("scope"))
    }

}
