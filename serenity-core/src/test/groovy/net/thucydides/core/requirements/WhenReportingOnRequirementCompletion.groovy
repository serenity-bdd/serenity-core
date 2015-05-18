package net.thucydides.core.requirements

import net.thucydides.core.issues.IssueTracking
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestResult
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.requirements.model.Requirement
import net.thucydides.core.requirements.reports.RequirementOutcome
import spock.lang.Specification

class WhenReportingOnRequirementCompletion extends Specification {

    def passingTest = Mock(TestOutcome)
    def failingTest = Mock(TestOutcome)
    def skippedTest = Mock(TestOutcome)
    def ignoredTest = Mock(TestOutcome)
    def pendingTest = Mock(TestOutcome)
    def requirement = Mock(Requirement)
    def issueTracking = Mock(IssueTracking)

    def setup() {
        passingTest.getResult()>>TestResult.SUCCESS
        failingTest.getResult()>>TestResult.FAILURE
        skippedTest.getResult()>>TestResult.SKIPPED
        ignoredTest.getResult()>>TestResult.IGNORED
        pendingTest.getResult()>>TestResult.PENDING
    }

    def "a requirement with no tests is not complete"() {
        when:
            def outcome = new RequirementOutcome(requirement, TestOutcomes.withNoResults(), issueTracking)
        then:
            !outcome.isComplete()
    }

    def "a requirement with only passing tests is complete"() {
        when:
            def outcome = new RequirementOutcome(requirement, TestOutcomes.of([passingTest]), issueTracking)
        then:
            outcome.isComplete()
    }

    def "a requirement with pending tests is not complete"() {
        when:
            def outcome = new RequirementOutcome(requirement, TestOutcomes.of([pendingTest, passingTest]), issueTracking)
        then:
            !outcome.isComplete()
    }

    def "a requirement with failing tests is not complete"() {
        when:
            def outcome = new RequirementOutcome(requirement, TestOutcomes.of([failingTest, passingTest]), issueTracking)
        then:
            !outcome.isComplete()
    }

    def "a requirement with only skipped tests is not complete"() {
        when:
            def outcome = new RequirementOutcome(requirement, TestOutcomes.of([skippedTest]), issueTracking)
        then:
            !outcome.isComplete()
    }
}
