package net.serenitybdd.core.parallel;

/**
 * An agent is an actor or task that can keep track of events in a separate step listener.
 * Agents are used for parallel processing within a test.
 */
public interface Agent {
    String IN_THE_CURRENT_SESSION = "$AGENT";
    String getId();
    String getName();
}
