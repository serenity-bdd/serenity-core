package net.serenitybdd.core.parallel;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Agency keeps track of multiple listeners assigned to different agents (which represent actors or activities), to facilitate parallel execution of test tasks.
 * Agents can run in parallel and are used to group steps reported by the Serenity Event Bus.
 */
public class Agency {

    private static final Agency INSTANCE = new Agency();

    private final Map<Agent, BaseStepListener> listeners = new ConcurrentHashMap<>();

    protected Agency() {}

    public static Agency getInstance() {
        return INSTANCE;
    }

    public void registerAgent(Agent agent) {
        listeners.put(agent, StepEventBus.getParallelEventBus().getBaseStepListener().spawn(agent.getName()));
    }

    public Optional<BaseStepListener> baseListenerFor(Agent agent) {
        return Optional.ofNullable(listeners.get(agent));
    }

    public void dropAgent(Agent agent) {
        listeners.remove(agent);
    }

    public Optional<BaseStepListener> currentAgentSpecificListener() {
        if (Serenity.sessionVariableCalled(Agent.IN_THE_CURRENT_SESSION) != null) {
            return baseListenerFor(Serenity.sessionVariableCalled(Agent.IN_THE_CURRENT_SESSION));
        } else {
            return Optional.empty();
        }
    }
}
