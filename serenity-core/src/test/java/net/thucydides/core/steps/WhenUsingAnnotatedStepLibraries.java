package net.thucydides.core.steps;

import net.thucydides.core.annotations.InvalidStepsFieldException;
import net.serenitybdd.annotations.Step;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.model.reflection.FieldSetter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

public class WhenUsingAnnotatedStepLibraries {

    class StepLibrary extends ScenarioSteps {

        public StepLibrary(final Pages pages) {
            super(pages);
        }

        @Step
        public void step1() {}

        @Step
        public void step2() {}
    }

    class UserStory {

        @Steps
        public StepLibrary stepLibrary;

        public StepLibrary unannotatedStepLibrary;

    }

    class UserStoryWithWrongStepType {

        @Steps
        public Object stepLibrary;

        public StepLibrary unannotatedStepLibrary;

    }

   class UserStoryWithNoSteps {

        public List stepLibrary;

        public StepLibrary unannotatedStepLibrary;

    }

    @Test
    public void should_find_annotated_step_library() {
        List<StepsAnnotatedField> stepsFields = StepsAnnotatedField.findOptionalAnnotatedFields(UserStory.class);

        assertThat(stepsFields.size(), is(1));
    }


    @Test
    public void annotated_step_library_can_be_of_any_type() {
        List<StepsAnnotatedField> stepsFields = StepsAnnotatedField.findOptionalAnnotatedFields(UserStoryWithWrongStepType.class);

        assertThat(stepsFields.size(), is(1));
    }


    @Test(expected=InvalidStepsFieldException.class)
    public void step_index_must_have_an_annotated_step_provided() {
        StepsAnnotatedField.findMandatoryAnnotatedFields(UserStoryWithNoSteps.class);
    }

    @Mock FieldSetter fieldSetter;
    @Mock ScenarioSteps scenarioSteps;
    @Mock Object testCase;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    class TestStepsAnnotatedField extends StepsAnnotatedField {

        TestStepsAnnotatedField(Field field) {
            super(field);
        }

        @Override
        protected FieldSetter set(Object targetObject) {
            return fieldSetter;
        }
    }

    @Test(expected = InvalidStepsFieldException.class)
    public void should_throw_exception_if_pages_object_field_cannot_be_accessed() throws Exception {

        doThrow(new IllegalAccessException()).when(fieldSetter).to(any());

        Field field = null; // value ignored
        TestStepsAnnotatedField testField = new TestStepsAnnotatedField(field);
        testField.setValue(testCase, scenarioSteps);
    }





}
