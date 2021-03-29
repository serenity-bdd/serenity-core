package serenitycore.net.thucydides.core.steps

import serenitycore.net.thucydides.core.steps.StepEventBus
import spock.lang.Specification

class WhenUsingStickyEventBuses extends Specification {

    def "A sticky event bus is tied to a key value, not to a thread"() {
        given:
        StepEventBus eventBus1 = StepEventBus.eventBusFor("Feature 1")
        when:
        StepEventBus eventBus2 = StepEventBus.eventBusFor("Feature 2")
        then:
        eventBus1 != eventBus2
    }

    def "A sticky event bus is tied to a key of any type"() {
        given:
        StepEventBus eventBus1 = StepEventBus.eventBusFor("My Feature")
        when:
        StepEventBus eventBus2 = StepEventBus.eventBusFor("My Feature")
        then:
        eventBus1 == eventBus2
    }

    def "A sticky event bus can be retrieved from different threads"() {
        given:
        StepEventBus eventBus1
        StepEventBus eventBus2
        when:
        Thread t1 = Thread.start { eventBus1 = StepEventBus.eventBusFor("Feature 1") }
        Thread t2 = Thread.start { eventBus2 = StepEventBus.eventBusFor("Feature 1") }

        t1.join()
        t2.join()
        then:
        eventBus1 == eventBus2
    }

    def "A non-sticky event bus is tied to a thread"() {
        given:
            StepEventBus eventBusA1
            StepEventBus eventBusA2
            StepEventBus eventBusB1
        when:
            Thread t1 = Thread.start {
                eventBusA1 = StepEventBus.getEventBus()
                eventBusA2 = StepEventBus.getEventBus()
            }
            Thread t2 = Thread.start { eventBusB1 = StepEventBus.getEventBus() }

            t1.join()
            t2.join()
        then:
            eventBusA1 != eventBusB1
        and:
            eventBusA1 == eventBusA2

    }

}
