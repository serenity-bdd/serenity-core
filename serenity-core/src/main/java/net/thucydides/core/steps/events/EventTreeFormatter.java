package net.thucydides.core.steps.events;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EventTreeFormatter {

    private static final Map<Class<?>, Set<Class<?>>> SYMMETRIC_EVENTS = Map.of(
            ExampleStartedEvent.class, Set.of(ExampleFinishedEvent.class),
            TestStartedEvent.class, Set.of(TestFinishedEvent.class, TestFailedEvent.class),
            StepStartedEvent.class, Set.of(
                    StepFinishedEvent.class,
                    StepFailedEvent.class,
                    StepIgnoredEvent.class,
                    StepPendingEvent.class
            )
    );

    private static final Set<Class<?>> STEP_COMPLETION_EVENTS = Set.of(
        StepFinishedEvent.class,
        StepFailedEvent.class,
        StepIgnoredEvent.class,
        StepPendingEvent.class
    );

    private static final String VERTICAL_LINE = "│   ";
    private static final String BRANCH_LINE = "├── ";
    private static final String LAST_BRANCH_LINE = "└── ";
    
    public static String formatEventTree(List<StepEventBusEvent> events) {
        StringBuilder builder = new StringBuilder();
        int depth = 0;
        
        for (int i = 0; i < events.size(); i++) {
            StepEventBusEvent event = events.get(i);
            boolean isLast = i == events.size() - 1;
            
            // If it's a start event, print it and increase depth for subsequent events
            if (isStartEvent(event)) {
                appendEventLine(builder, event, depth, false);
                depth++;
            }
            // If it's a finish event, decrease depth first then print it
            else if (isFinishEvent(event)) {
                depth--;
                appendEventLine(builder, event, depth, isLast);
            }
            // For other events, print at current depth
            else {
                appendEventLine(builder, event, depth, false);
            }
        }
        
        return builder.toString();
    }

    private static boolean isStartEvent(StepEventBusEvent event) {
        return SYMMETRIC_EVENTS.containsKey(event.getClass());
    }

    private static boolean isFinishEvent(StepEventBusEvent event) {
        if (event.getClass().getSimpleName().equals("StepFinishedWithResultEvent")) {
            return true;
        }
        return SYMMETRIC_EVENTS.values().stream().anyMatch(completionEvents -> completionEvents.contains(event.getClass())) ||
                STEP_COMPLETION_EVENTS.contains(event.getClass());
    }

    private static void appendEventLine(StringBuilder builder, StepEventBusEvent event, int depth, boolean isLast) {
        // Add vertical lines for each level of depth
        for (int i = 0; i < depth; i++) {
            builder.append(VERTICAL_LINE);
        }
        
        // Add the branch line (different for last item in a group)
        builder.append(isLast ? LAST_BRANCH_LINE : BRANCH_LINE);
        
        // Add the event name (simplified to just the class name without "Event" suffix)
        String eventName = event.getClass().getSimpleName();
        if (eventName.endsWith("Event")) {
            eventName = eventName.substring(0, eventName.length() - 5);
        }
        builder.append(eventName);
        
        if (event instanceof StepStartedEvent) {
            StepStartedEvent stepEvent = (StepStartedEvent) event;
            builder.append(": ").append(stepEvent.stepDescription.getTitle());
        } else if (event instanceof TestStartedEvent) {
            TestStartedEvent testEvent = (TestStartedEvent) event;
            builder.append(": ").append(testEvent.getTestName());
        }
        
        builder.append("\n");
    }
}