package net.thucydides.model.domain

import net.thucydides.model.reports.TestOutcomes
import spock.lang.Specification

class WhenExtractingTagsFromTestOutcomes extends Specification {

    def outcome1 = TestOutcome.forTestInStory("someTest 1", Story.withId("1","story"))
    def outcome2 = TestOutcome.forTestInStory("someTest 2", Story.withId("1","story"))
    def outcome3 = TestOutcome.forTestInStory("someTest 3", Story.withId("1","story"))
    def outcome4 = TestOutcome.forTestInStory("someTest 4", Story.withId("1","story"))
    def outcome5 = TestOutcome.forTestInStory("someTest 5", Story.withId("1","story"))

    def release1 = TestTag.withName("PROJ Release 1").andType("version")
    def release2 = TestTag.withName("PROJ Release 2").andType("version")
    def iteration1 = TestTag.withName("Iteration 12").andType("version")
    def iteration2 = TestTag.withName("Iteration 13").andType("version")
    def iteration3 = TestTag.withName("Iteration 22").andType("version")
    def issue1 = TestTag.withName("MyIssue").andType("issue")

    def setup() {
        outcome1.addTags([release1,iteration1])
        outcome2.addTags([release1, iteration1])
        outcome3.addTags([release1, iteration2])
        outcome4.addTags([release2, iteration3])
        outcome5.addTags([issue1])
        def issues = new ArrayList<String>();
        issues.add("MyIssue");
        outcome5.addIssues(issues);
    }

    def "should extract tags from test outcomes"() {
        given:
            def outcomes = TestOutcomes.of([outcome1, outcome2, outcome3, outcome4])
        when:
            def releases = outcomes.findMatchingTags().
                                    withName(org.hamcrest.Matchers.containsString("Release")).
                                    withType(org.hamcrest.Matchers.is("version")).list();
        then:
            releases == [release1, release2]
    }

    def "should extract tags from a nested test outcomes"() {
        given:
            def outcomes = TestOutcomes.of([outcome1, outcome2, outcome3, outcome4])
        when:
            def release1Outcomes = outcomes.withTag(release1)
            def iterations = release1Outcomes.findMatchingTags().
                                                withName(org.hamcrest.Matchers.containsString("Iteration")).
                                                withType("version").list();
        then:
            iterations == [iteration1, iteration2]
    }

    def "should not create duplicate test issue outcomes"() {
        given:
            def outcomes = TestOutcomes.of([outcome1, outcome2, outcome3, outcome4, outcome5])
        when:
            outcomes = outcomes.withTag(issue1);
        then:
            outcomes.getOutcomes().size() == 1
    }
}
