package net.thucydides.core.steps;

import net.serenitybdd.core.pages.PagesAnnotatedField;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.construction.StepsClassResolver;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_MAXIMUM_STEP_NESTING_DEPTH;

/**
 * Utility class used to inject fields into a test case.
 */
public final class StepAnnotations {

    private final EnvironmentVariables environmentVariables;
    private final List<StepsClassResolver> stepsClassResolvers;
    private static final Logger LOGGER = LoggerFactory.getLogger(StepAnnotations.class);

    private StepAnnotations() {
        this(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    private StepAnnotations(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.stepsClassResolvers = new ArrayList<>();
        Iterator<StepsClassResolver> resolverIterator = ServiceLoader.load(StepsClassResolver.class).iterator();
        while (resolverIterator.hasNext()) {
            this.stepsClassResolvers.add(resolverIterator.next());
        }
    }

    public static StepAnnotations injector() {
        return new StepAnnotations();
    }

    private static StepAnnotations withEnvironmentVariables(EnvironmentVariables environmentVariables) {
        return new StepAnnotations(environmentVariables);
    }

    /**
     * Instantiates the step scenario fields in a test case.
     */
    public void injectScenarioStepsInto(final Object testCase, final StepFactory stepFactory) {
        List<StepsAnnotatedField> stepsFields = StepsAnnotatedField.findOptionalAnnotatedFields(testCase.getClass());
        instanciateScenarioStepFields(testCase, stepFactory, stepsFields);
    }

    /**
     * Instantiates the step scenario fields in a test case.
     */
    void injectNestedScenarioStepsInto(final Object scenarioSteps,
                                       final StepFactory stepFactory,
                                       final Class<?> scenarioStepsClass) {
        List<StepsAnnotatedField> stepsFields = StepsAnnotatedField.findOptionalAnnotatedFields(scenarioStepsClass);
        instanciateScenarioStepFields(scenarioSteps, stepFactory, stepsFields);
    }


    private void instanciateScenarioStepFields(final Object testCaseOrSteps,
                                               final StepFactory stepFactory,
                                               final List<StepsAnnotatedField> stepsFields) {
        for (StepsAnnotatedField stepsField : stepsFields) {
            instantiateAnyUnitiaializedSteps(testCaseOrSteps, stepFactory, stepsField);
        }
    }

    public void instrumentStepsInField(Object target, Field field, StepFactory stepFactory) {
        StepsAnnotatedField annotatedField = new StepsAnnotatedField(field);
        instantiateAnyUnitiaializedSteps(target, stepFactory, annotatedField);
    }

    private void instantiateAnyUnitiaializedSteps(Object testCaseOrSteps,
                                                  StepFactory stepFactory,
                                                  StepsAnnotatedField stepsField) {
        if (!stepsField.isInstantiated(testCaseOrSteps)) {

            ensureThatThisFieldIsNotCyclicOrRecursive(stepsField);

            Class<?> scenarioStepsClass = resolveStepsClass(stepsField.getFieldClass());

            Object steps = StepLibraryCreator.usingConfiguredCreationStrategy(stepFactory, stepsField, environmentVariables)
                                             .initiateStepsFor(scenarioStepsClass);

            stepsField.setValue(testCaseOrSteps, steps);
            stepsField.assignActorNameIn(steps);
            injectNestedScenarioStepsInto(steps, stepFactory, scenarioStepsClass);
        }
    }

    private Class<?> resolveStepsClass(Class<?> originalStepsClass) {
        if (stepsClassResolvers.isEmpty()) {
            return originalStepsClass;
        }

        for (StepsClassResolver stepsClassResolver : stepsClassResolvers) {
            Class<?> resolvedClass = stepsClassResolver.resolveStepsClass(originalStepsClass);
            if (resolvedClass != null) {
                LOGGER.debug("Steps {} will be instantiated as {}", originalStepsClass, resolvedClass);
                return resolvedClass;
            }
        }

        return originalStepsClass;
    }

    private void ensureThatThisFieldIsNotCyclicOrRecursive(StepsAnnotatedField stepsAnnotatedField) {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        long levelsOfNesting = Stream.of(stackTrace).filter(element -> element.getMethodName().equals("instantiateAnyUnitiaializedSteps"))
                .count();

        int maxAllowedNesting = SERENITY_MAXIMUM_STEP_NESTING_DEPTH.integerFrom(environmentVariables, 32);
        if (levelsOfNesting > maxAllowedNesting) {
            String message = String.format(
                    "A recursive or cyclic reference was detected for the @Steps-annotated field %s in class %s. " +
                            "You may need to use @Steps(shared=true) to ensure that the same step library instance is used everywhere.",
                    stepsAnnotatedField.getFieldName(), stepsAnnotatedField.getFieldClass().getName());
            throw new RecursiveOrCyclicStepLibraryReferenceException(message);
        }
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     */
    public void injectAnnotatedPagesObjectInto(final Object testCase, final Pages pages) {
        Optional<PagesAnnotatedField> pagesField = PagesAnnotatedField.findFirstAnnotatedField(testCase.getClass());
        if (pagesField.isPresent()) {
            pages.setDefaultBaseUrl(pagesField.get().getDefaultBaseUrl());
            pagesField.get().setValue(testCase, pages);
        }
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver, if the field is present.
     */
    public void injectOptionalAnnotatedPagesObjectInto(final Object testCase, final Pages pages) {
        Optional<PagesAnnotatedField> pagesField = PagesAnnotatedField.findOptionalAnnotatedField(testCase.getClass());
        if (pagesField.isPresent()) {
            pages.setDefaultBaseUrl(pagesField.get().getDefaultBaseUrl());
            pagesField.get().setValue(testCase, pages);
        }
    }

}
