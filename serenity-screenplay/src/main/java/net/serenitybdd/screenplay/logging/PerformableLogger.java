package net.serenitybdd.screenplay.logging;

import com.google.common.eventbus.Subscribe;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.events.ActorAsksQuestion;
import net.serenitybdd.screenplay.events.ActorBeginsPerformanceEvent;
import net.serenitybdd.screenplay.events.ActorEndsPerformanceEvent;
import net.serenitybdd.screenplay.events.ActorPerforms;
import net.thucydides.core.steps.StepName;
import net.thucydides.model.steps.ReplaceField;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class PerformableLogger {
    int level = 0;
    String currentActor;

    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    private Logger logger;

    public PerformableLogger() {
        logger = LoggerFactory.getLogger("screenplay");
    }

    @Subscribe
    public void beginPerformance(ActorBeginsPerformanceEvent performanceEvent) {
        currentActor = performanceEvent.getName();
        level++;
    }

    @Subscribe
    public void endPerformance(ActorEndsPerformanceEvent performanceEvent) {
        level--;
    }

    @Subscribe
    public void perform(ActorPerforms performAction) {
        String stepTitle = "";
        if (isInstrumented(performAction.getPerformable())) {
            stepTitle = titleFromParentOf(performAction.getPerformable()).replace("{0}", currentActor);
        } else {
            stepTitle = performAction.getPerformable().toString().replace("{0}", currentActor);
        }
        logger.info(ANSI_GREEN + indentation(level) + " " + stepTitle + ANSI_RESET);
    }

    private String indentation(int level) {
        return "|" + StringUtils.repeat("-", level * 2);
    }

    private String titleFromParentOf(Performable performable) {
        try {
            Method performAs = performable.getClass().getSuperclass().getMethod("performAs", Actor.class);
            Optional<String> stepName = StepName.fromStepAnnotationIn(performAs);
            if (stepName.isPresent()) {
                Map<String, Object> fields = fieldValuesIn(performable);
                return replaceFieldTokensWithFieldValuesIn(stepName.get(),fields);
            } else {
                return performable.getClass().getSuperclass().getName();
            }
        } catch (NoSuchMethodException | IllegalAccessException e) {
            return performable.getClass().getSuperclass().getName();
        }
    }

    private Map<String, Object> fieldValuesIn(Performable performable) throws IllegalAccessException {
        Map<String, Object> fieldValues = new HashMap<>();

        for(Field field : allFieldsIn(performable.getClass())) {
            field.setAccessible(true);
            fieldValues.put(field.getName(), "" + field.get(performable));
        }
        return fieldValues;
    }

    private List<Field> allFieldsIn(Class<?> someClass) {
        return getFields(someClass);
    }

    private String replaceFieldTokensWithFieldValuesIn(String description, Map<String, Object> fields) {
            for(String field : fields.keySet()) {
                description = ReplaceField.in(description).theFieldCalled(field).with(fields.get(field));
            }
            return description;
    }

    public static List<Field> getFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> classToInspect = clazz;
        while (classToInspect != null) {
            fields.addAll(Arrays.asList(classToInspect.getDeclaredFields()));
            classToInspect = classToInspect.getSuperclass();
        }
        return fields;
    }

    private boolean isInstrumented(Performable performable) {
        return performable.toString().contains("$ByteBuddy");
    }

    @Subscribe
    public void prepareQuestion(ActorAsksQuestion questionEvent) {
        System.out.println("Question " + questionEvent.getQuestion());
    }
}
