package net.thucydides.model.domain

import net.serenitybdd.annotations.Version
import org.junit.Test
import spock.lang.Specification

class WhenAssociatingAVersionWithAnOutcome extends Specification {

    def "should be able to store a version number with a test outcome"() {
        given:
            def outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story"));
        when:
            outcome.addVersion("Version 1")
        then:
            outcome.versions.contains("Version 1")
    }

    final static class VersionedSample {

        @Test
        @Version("Version 1.1")
        public void someTest() {}
    }

    def "should be able to get a version number from an annotation"() {
        when:
            def outcome = TestOutcome.forTest("someTest", VersionedSample)
        then:
            outcome.versions.contains("Version 1.1")
    }

    @Version("Release 1")
    final static class ClassWithOverallVersionSample {

        @Test
        @Version("Version 1.1")
        public void someTest() {}
    }

    def "should be able to get a version number from a class-level annotation"() {
        when:
            def outcome = TestOutcome.forTest("someTest", ClassWithOverallVersionSample)
        then:
            outcome.versions.contains("Version 1.1")
        and:
            outcome.versions.contains("Release 1")
    }

}
