package net.thucydides.core.steps.session;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.events.EventTreeFormatter;
import net.thucydides.core.steps.events.StepEventBusEvent;
import net.thucydides.core.steps.events.StepFailedEvent;
import net.thucydides.core.steps.events.StepStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * A Test Session corresponds to a single threaded test case.
 * Is started from a Cucumber "testCaseStarted" event.
 * An Actor can check if a TestSession is open and add there StepEventBusEvents to be serialized.
 */
public class TestSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestSession.class);

    private static ThreadLocal<TestSessionContext> sessionContext = ThreadLocal.withInitial(() -> new TestSessionContext());

    public static void startSession(String sessionId, StepEventBus stepEventBus) {
        sessionContext.get().getSessionStarted().set(true);
        sessionContext.get().setSessionId(sessionId);
        sessionContext.get().setStepEventBus(stepEventBus);
        LOGGER.debug("SRP:SessionStart: id " + sessionId);
    }

    public static void closeSession() {
        sessionContext.get().getSessionStarted().set(false);
        LOGGER.debug("SRP:SessionEnd: id " + sessionContext.get().getSessionId());
    }

    public static TestSessionContext getTestSessionContext() {
        return sessionContext.get();
    }

    public static void cleanupSession() {
        sessionContext.get().getStepEventBusEvents().clear();
    }

    public static boolean isSessionStarted() {
        return sessionContext.get().getSessionStarted().get();
    }

    public static void addEvent(StepEventBusEvent event) {
        LOGGER.debug("SRP:SessionAddEvent: id " + sessionContext.get().getSessionId() + " " + event);
        sessionContext.get().addStepBusEvent(event);
    }

    public static boolean currentStepHasFailed() {
        List<StepEventBusEvent> events = sessionContext.get().getStepEventBusEvents();
        for (int i = events.size() - 1; i >= 0; i--) {
            if (events.get(i) instanceof StepStartedEvent) {
                break;
            }
            if (events.get(i) instanceof StepFailedEvent) {
                return true;
            }
        }
        return false;
    }

    public static List<StepEventBusEvent> getSessionEvents() {
        return sessionContext.get().getStepEventBusEvents();
    }

    public static String asTree() {
        return EventTreeFormatter.formatEventTree(getSessionEvents());
    }
}
