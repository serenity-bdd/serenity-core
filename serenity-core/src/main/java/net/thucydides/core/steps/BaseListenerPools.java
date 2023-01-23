package net.thucydides.core.steps;

import net.serenitybdd.core.Serenity;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class BaseListenerPools {

    /**
     * Maintain a set of separate base step listeners for running parallel activities within the same test.
     * For Screenplay tests, they will be indexed by actor
     */
    private static final Map<ThreadGroup, Map<String, BaseStepListener>> THREAD_GROUP_LISTENERS= new HashMap<>();

    /**
     * Use a new thread for each actor that participates in threads in the current thread group.
     */
    public static void useAThreadForEachActorIn(ThreadGroup currentThreadGroup) {
        Map<String, BaseStepListener> stepEventBusForParentThread = new HashMap<>();
        THREAD_GROUP_LISTENERS.put(currentThreadGroup, stepEventBusForParentThread);
    }

    public static boolean isUsingAThreadForEachActor() {
        return THREAD_GROUP_LISTENERS.containsKey(Thread.currentThread().getThreadGroup());
    }

    public static BaseStepListener baseStepListenerFor(StepEventBus eventBus) {
        Map<String, BaseStepListener> stepEventBusesPerThread = THREAD_GROUP_LISTENERS.get(Thread.currentThread().getThreadGroup());
        if (!stepEventBusesPerThread.containsKey(currentActivityIdentifier())) {
            stepEventBusesPerThread.put(currentActivityIdentifier(), mainStepListener().childListenerFor(eventBus));
        }
        return stepEventBusesPerThread.get(Thread.currentThread());
    }

    private static String currentActivityIdentifier() {
        return Serenity.getCurrentSession().get("ACTIVITY").toString();
    }

    private static BaseStepListener mainStepListener() {
        return THREAD_GROUP_LISTENERS.get(Thread.currentThread().getThreadGroup()).entrySet()
                .stream()
//                .filter(entry -> entry.getKey().getName().equals("main"))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No main thread found"));
    }

    private static Stream<BaseStepListener> childStepListeners() {
        return THREAD_GROUP_LISTENERS.get(Thread.currentThread().getThreadGroup()).entrySet()
                .stream()
//                .filter(entry -> !entry.getKey().getName().equals("main"))
                .map(Map.Entry::getValue);
    }

    public static void mergeParallelActivityFlows(ThreadGroup currentThreadGroup) {
        BaseStepListener mainStepListener = mainStepListener();

//        childStepListeners().forEach(
//                baseStepListener -> {
//                    System.out.println(baseStepListener);
//                }
//        );
        THREAD_GROUP_LISTENERS.remove(currentThreadGroup);
    }


}
