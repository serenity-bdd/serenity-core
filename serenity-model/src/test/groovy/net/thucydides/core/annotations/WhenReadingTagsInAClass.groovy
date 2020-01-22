package net.thucydides.core.annotations

import net.thucydides.core.model.TestTag
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

}
