package net.thucydides.core.steps;

import net.thucydides.core.reflection.MethodFinder;

import java.lang.reflect.Method;
import java.util.*;

import static io.vavr.API.List;
import static net.thucydides.core.util.NameConverter.humanize;
import static net.thucydides.core.util.NameConverter.withNoArguments;

/**
 * A description of a step executed during a Thucydides step run.
 * Used in the reporting to generate user-readable names for the executed steps.
 */
public class ExecutedStepDescription implements Cloneable {

    private final Class<? extends Object> stepsClass;
    private final String name;
    private final List<Object> argumentsList;
    private final List<String> convertedArgumentsList;
    private final Map<String, Object> displayedFields;
    private boolean isAGroup;

    private final static Map<String,Object> NO_FIELDS = new HashMap();
    private final static List<Object> NO_ARGUMENTS = new ArrayList<>();
    private boolean isAQuestion;

    protected ExecutedStepDescription(final Class<? extends Object> stepsClass,
                                      final String name,
                                      List<Object> argumentsList,
                                      Map<String, Object> displayedFields,
                                      final boolean isAGroup) {
        this.stepsClass = stepsClass;
        this.name = name;
        this.argumentsList = argumentsList;
        this.convertedArgumentsList = convertArguments(this.argumentsList.toArray());
        this.displayedFields = displayedFields;
        this.isAGroup = isAGroup;
        this.isAQuestion = false;
    }

    protected ExecutedStepDescription(final Class<? extends Object> stepsClass,
                                      final String name,
                                      final boolean isAGroup) {
        this(stepsClass, name, NO_ARGUMENTS, NO_FIELDS, isAGroup);
    }

    protected ExecutedStepDescription(final Class<? extends Object> stepsClass,
                                      final String name) {
        this(stepsClass, name, NO_ARGUMENTS, NO_FIELDS, false);
    }

    protected ExecutedStepDescription(final String name) {
        this(null, name, NO_ARGUMENTS, NO_FIELDS, false);
    }

    public ExecutedStepDescription clone() {
        return new ExecutedStepDescription(stepsClass, name, argumentsList, displayedFields, isAGroup);
    }

    /**
     * The class of the step library being executed.
     */
    public Class<? extends Object> getStepClass() {
        return stepsClass;
    }

    public String getName() {
        return name;
    }

    public List<String> getArguments() {
        return convertedArgumentsList;
    }

    public List<Object> getRawArguments() {
        return argumentsList;
    }

    public ExecutedStepDescription withName(String newName) {
        return new ExecutedStepDescription(this.stepsClass, newName, isAGroup);
    }

    public ExecutedStepDescription withDisplayedFields(Map<String, Object> displayedFields) {
        return new ExecutedStepDescription(this.stepsClass, this.name, this.argumentsList, displayedFields, isAGroup);
    }

    /**
     * We might not have the test class provided (e.g. at the end of a test).
     */
    public static ExecutedStepDescription of(final Class<? extends Object> stepsClass,
                                             final String name) {
        return new ExecutedStepDescription(stepsClass, name, NO_ARGUMENTS, NO_FIELDS, false);
    }

    public static ExecutedStepDescription of(final Class<? extends Object> stepsClass,
                                             final String name,
                                             final Object[] arguments) {
        return new ExecutedStepDescription(stepsClass, name, Arrays.asList(arguments), NO_FIELDS, false);
    }

    private static List<String> convertArguments(Object[] arguments) {
        return List(arguments).map(StepArgumentWriter::readableFormOf).asJava();
    }

    public static ExecutedStepDescription withTitle(final String name) {
        return new ExecutedStepDescription(name);
    }

    public boolean isAGroup() {
        return isAGroup;
    }

    public void setAGroup(final boolean aGroup) {
        isAGroup = aGroup;
    }

    public Method getStepMethod() {
        if (getStepClass() != null) {
            if (ScreenplayInspector.isAScreenplayClass(getStepClass())) {
                return ScreenplayInspector.performAsMethodIn(getStepClass());
            } else {
                return methodCalled(withNoArguments(getName()), getStepClass());
            }
        } else {
            return null;
        }
    }

    public boolean isAQuestion() {
        return isAQuestion;
    }

    private Method methodCalled(final String methodName, final Class<?> testClass) {
        Method method = MethodFinder.inClass(testClass).getMethodNamed(methodName);
        if (method == null) {
            throw new IllegalArgumentException("No test method called " + methodName + " was found in " + testClass);
        }
        return method;
    }


    /**
     * Turns a method into a human-readable title.
     */
    public String getTitle() {
        try {
            return humanize(AnnotatedStepDescription.from(this).getName());
        } catch(IllegalArgumentException noMatchingMethod) {
            return humanize(name);
        }
    }

    public Map<String, Object> getDisplayedFields() {
        return new HashMap<>(displayedFields);
    }

    public ExecutedStepDescription asAQuestion() {
        isAQuestion = true;
        return this;
    }
}
