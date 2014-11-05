package net.thucydides.core.steps;

import com.google.common.base.Optional;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.PagesAnnotatedField;

import java.util.List;

/**
 * Utility class used to inject fields into a test case.
 * @author johnsmart
 *
 */
public final class StepAnnotations {
    
    private StepAnnotations() {}

    /**
     * Instantiates the step scenario fields in a test case.
     */
    public static void injectScenarioStepsInto(final Object testCase, final StepFactory stepFactory) {
        List<StepsAnnotatedField> stepsFields = StepsAnnotatedField.findOptionalAnnotatedFields(testCase.getClass());
        instanciateScenarioStepFields(testCase, stepFactory, stepsFields);
     }

    /**
     * Instantiates the step scenario fields in a test case.
     */
    public static void injectNestedScenarioStepsInto(final Object scenarioSteps,
                                                     final StepFactory stepFactory,
                                                     final Class<?> scenarioStepsClass) {
        List<StepsAnnotatedField> stepsFields = StepsAnnotatedField.findOptionalAnnotatedFields(scenarioStepsClass);
        instanciateScenarioStepFields(scenarioSteps, stepFactory, stepsFields);
     }


    private static void instanciateScenarioStepFields(
            final Object testCaseOrSteps, final StepFactory stepFactory,
            final List<StepsAnnotatedField> stepsFields) {
        for(StepsAnnotatedField stepsField : stepsFields) {
            instantiateAnyUnitiaializedSteps(testCaseOrSteps, stepFactory, stepsField);
        }
    }

    private static void instantiateAnyUnitiaializedSteps(Object testCaseOrSteps,
                                                         StepFactory stepFactory,
                                                         StepsAnnotatedField stepsField) {
        if (!stepsField.isInstantiated(testCaseOrSteps)) {
           Class<?> scenarioStepsClass = stepsField.getFieldClass();
           Object steps = stepFactory.getStepLibraryFor(scenarioStepsClass);
           stepsField.setValue(testCaseOrSteps, steps);
           injectNestedScenarioStepsInto(steps, stepFactory, scenarioStepsClass);
       }
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     */
    public static void injectAnnotatedPagesObjectInto(final Object testCase, final Pages pages) {
       PagesAnnotatedField pagesField = PagesAnnotatedField.findFirstAnnotatedField(testCase.getClass());
       pages.setDefaultBaseUrl(pagesField.getDefaultBaseUrl());
       pagesField.setValue(testCase, pages);
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver, if the field is present.
     */
    public static void injectOptionalAnnotatedPagesObjectInto(final Object testCase, final Pages pages) {
        Optional<PagesAnnotatedField> pagesField = PagesAnnotatedField.findOptionalAnnotatedField(testCase.getClass());
        if (pagesField.isPresent()) {
            pages.setDefaultBaseUrl(pagesField.get().getDefaultBaseUrl());
            pagesField.get().setValue(testCase, pages);
        }
    }

}
