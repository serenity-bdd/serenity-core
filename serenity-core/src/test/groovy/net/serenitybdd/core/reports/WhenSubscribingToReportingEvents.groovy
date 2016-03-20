package net.serenitybdd.core.reports

import com.google.common.eventbus.Subscribe
import net.serenitybdd.core.eventbus.Broadcaster
import net.serenitybdd.core.lifecycle.*
import net.thucydides.core.model.TestOutcome
import spock.lang.Specification

/**
 * We want to be able to subscribe to events about tests, in particular to know when a test has completed,
 * so that we can do more flexible reporting.
 */
class WhenSubscribingToReportingEvents extends Specification {

    static class MyReporter {

        String testName;
        TestOutcome received

        @Subscribe testStarted(TestStartedEvent event) {
            testName = event.name
        }

        @Subscribe testFinished(TestFinishedEvent event) {
            received = event.testOutcome
        }
    }

    TestOutcome testOutcome = Mock()
    TestOutcome testOutcome2 = Mock()


    def cleanup() {
        Broadcaster.shutdown()
    }
    def "a reporter class can subscribe to test lifecycle events"() {
        given: "I have written a custom reporter"
            def myReporter = new MyReporter()

        when: "I ask to be notified about test lifecycle events"
            Broadcaster.register(myReporter)

        and: "A test gets executed"
            Broadcaster.postEvent(TestLifecycle.aTestHasStartedCalled("some test"))
            Broadcaster.postEvent(TestLifecycle.aTestHasFinishedWith(testOutcome))

            Broadcaster.shutdown()

        then: "I should be told about the event"
            myReporter.received == testOutcome
        and:
            myReporter.testName == "some test"

    }

    static class MyGroupReporter {

        String testGroupName;
        List<TestOutcome> received

        @Subscribe testGroupStarted(TestGroupStartedEvent event) {
            testGroupName = event.name
        }

        @Subscribe testGroupFinished(TestGroupFinishedEvent event) {
            received = event.testOutcomes
        }
    }


    def "several reporters class can subscribe to test group lifecycle events"() {
        given:
            def myReporter = new MyGroupReporter()

        when:
            Broadcaster.register(myReporter)

        and:
            Broadcaster.postEvent(TestLifecycle.aTestGroupHasStartedCalled("some test group"))
            Broadcaster.postEvent(TestLifecycle.aTestGroupHasFinishedWith([testOutcome, testOutcome2]))

            Broadcaster.shutdown()

        then:
            myReporter.received == [testOutcome, testOutcome2]
        and:
            myReporter.testGroupName == "some test group"

    }


    def "several reporters class can subscribe to lifecycle events"() {
        given:
            def clarkKent = new MyReporter()
            def peterParker = new MyReporter()

        when:
            Broadcaster.register(clarkKent).register(peterParker)

        and:
            Broadcaster.postEvent(TestLifecycle.aTestHasStartedCalled("some test"))
            Broadcaster.postEvent(TestLifecycle.aTestHasFinishedWith(testOutcome))

            Broadcaster.shutdown()
        then:
            clarkKent.received == testOutcome
        and:
            peterParker.received == testOutcome

    }


    static class MySerialReporter {

        List<String> received = [];

        @Subscribe testStarted(TestStartedEvent event) {
            received.add event.name
        }
    }

    def "a reporter recieves all the lifecycle events sent"() {
        given:
            def clarkKent = new MySerialReporter()
            def peterParker = new MySerialReporter()

        when:
                Broadcaster.register(clarkKent).register(peterParker)

        and:
        for(i in 1..100) {
            Broadcaster.postEvent(TestLifecycle.aTestHasStartedCalled("Test $i"))
        }

        then:
            clarkKent.received.size() == 100
        and:
            peterParker.received.size() == 100

    }

    def "a reporter stops receiving events after the broadcast has shut down"() {
        given:
            def clarkKent = new MyReporter()
            def peterParker = new MyReporter()

        when:
            Broadcaster.register(clarkKent)
            Broadcaster.shutdown()

            Broadcaster.register(peterParker)
            Broadcaster.postEvent(TestLifecycle.aTestHasFinishedWith(testOutcome))
            Broadcaster.shutdown()

        then:
            !clarkKent.received
        and:
            peterParker.received == testOutcome

    }



}
