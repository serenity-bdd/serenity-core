package serenitycore.net.thucydides.core.bootstrap

import serenitycore.net.thucydides.core.bootstrap.ThucydidesContext
import serenitycore.net.thucydides.core.steps.Listeners
import serenitycore.net.thucydides.core.steps.StepEventBus
import spock.lang.Specification

class WhenConfiguringAThucydidesContext extends Specification {

    def "a context should have a base listener by default"() {
        when:
            def context = ThucydidesContext.newContext()
        then:
            StepEventBus.eventBus.baseStepListener == context.stepListener
    }

    def "a context can be configured with additional listeners"() {
        when:
            def context = ThucydidesContext.newContext(Optional.empty(), Listeners.loggingListener)
        then:
            StepEventBus.eventBus.registeredListeners.contains Listeners.loggingListener
    }
}
