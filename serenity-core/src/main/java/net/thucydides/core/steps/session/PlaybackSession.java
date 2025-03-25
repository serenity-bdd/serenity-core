package net.thucydides.core.steps.session;

import net.thucydides.core.steps.StepEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class PlaybackSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaybackSession.class);

    private static final ThreadLocal<PlaybackSessionContext> sessionContext = ThreadLocal.withInitial(PlaybackSessionContext::new);

    public static void startSession(String sessionId, StepEventBus stepEventBus) {
        sessionContext.get().getSessionStarted().set(true);
        sessionContext.get().setSessionId(sessionId);
        sessionContext.get().setStepEventBus(stepEventBus);
        LOGGER.debug("SRP:PlaybackSessionStart: id: {} " , sessionId);
    }

    public static void closeSession() {
        sessionContext.get().getSessionStarted().set(false);
        LOGGER.debug("SRP:PlaybackSessionEnd: id: {} ", sessionContext.get().getSessionId());
    }

    public static PlaybackSessionContext getTestSessionContext() {
        return sessionContext.get();
    }


    public static boolean isSessionStarted() {
        return sessionContext.get().getSessionStarted().get();
    }
}
