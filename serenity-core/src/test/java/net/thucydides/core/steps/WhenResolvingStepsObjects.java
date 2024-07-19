package net.thucydides.core.steps;

import net.serenitybdd.annotations.Steps;
import net.thucydides.core.steps.construction.StepsClassResolver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class WhenResolvingStepsObjects {

    private StepFactory stepFactory = StepFactory.getFactory();
    private StepAnnotations stepAnnotations = StepAnnotations.injector();

    private static final Map<Class, Class> classesStorage = new HashMap<>();

    public static class StepsClassResolverImpl implements StepsClassResolver {
        @Override
        public Class<?> resolveStepsClass(Class<?> original) {
            return classesStorage.get(original);
        }
    }

    interface InterfaceSteps {
        String step1();
    }

    static class InterfaceStepsImpl implements InterfaceSteps {
        @Override
        public String step1() {
            return "InterfaceStepsImpl: step1";
        }
    }

    static abstract class AbstractClassSteps {
        public abstract String step1();
    }

    static class AbstractClassStepsImpl extends AbstractClassSteps {
        @Override
        public String step1() {
            return "AbstractClassStepsImpl: step1";
        }
    }

    static class SimpleBaseSteps  {
        public String step1() {
            return "SimpleBaseSteps: step1";
        }
    }

    static class SimpleInheritedSteps extends SimpleBaseSteps {
        public String step1() {
            return "SimpleInheritedSteps: step1";
        }
    }

    static class TestCaseWithInterfaceSteps {
        @Steps
        InterfaceSteps interfaceSteps;
    }

    static class TestCaseWithAbstractSteps {
        @Steps
        AbstractClassSteps abstractClassSteps;
    }

    static class TestCaseWithRegularSteps {
        @Steps
        SimpleBaseSteps simpleBaseSteps;
    }

    @Before
    public void setUp() throws Exception {
        classesStorage.clear();
    }

    @Test
    public void should_resolve_implementation_for_interface_steps() {
        classesStorage.put(InterfaceSteps.class, InterfaceStepsImpl.class);

        TestCaseWithInterfaceSteps testCase = new TestCaseWithInterfaceSteps();
        stepAnnotations.injectScenarioStepsInto(testCase, stepFactory);

        Assert.assertEquals("InterfaceStepsImpl: step1", testCase.interfaceSteps.step1());
    }

    @Test
    public void should_resolve_implementation_for_abstract_steps() {
        classesStorage.put(AbstractClassSteps.class, AbstractClassStepsImpl.class);

        TestCaseWithAbstractSteps testCase = new TestCaseWithAbstractSteps();
        stepAnnotations.injectScenarioStepsInto(testCase, stepFactory);

        Assert.assertEquals("AbstractClassStepsImpl: step1", testCase.abstractClassSteps.step1());
    }

    @Test
    public void should_resolve_implementation_for_inherited_steps() {
        classesStorage.put(SimpleBaseSteps.class, SimpleInheritedSteps.class);

        TestCaseWithRegularSteps testCase = new TestCaseWithRegularSteps();
        stepAnnotations.injectScenarioStepsInto(testCase, stepFactory);

        Assert.assertEquals("SimpleInheritedSteps: step1", testCase.simpleBaseSteps.step1());
    }

    @Test
    public void should_resolve_base_class_if_not_instructions_for_resolver() {
        TestCaseWithRegularSteps testCase = new TestCaseWithRegularSteps();
        stepAnnotations.injectScenarioStepsInto(testCase, stepFactory);

        Assert.assertEquals("SimpleBaseSteps: step1", testCase.simpleBaseSteps.step1());
    }

}
