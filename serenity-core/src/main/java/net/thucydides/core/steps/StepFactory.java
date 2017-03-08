package net.thucydides.core.steps;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.serenitybdd.core.di.DependencyInjector;
import net.serenitybdd.core.exceptions.StepInitialisationException;
import net.serenitybdd.core.injectors.EnvironmentDependencyInjector;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.construction.ConstructionStrategy;
import net.thucydides.core.steps.construction.StepLibraryConstructionStrategy;
import net.thucydides.core.steps.construction.StepLibraryType;
import net.thucydides.core.steps.di.DependencyInjectorService;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

import static com.google.common.collect.ImmutableSet.copyOf;
import static net.thucydides.core.steps.construction.ConstructionStrategy.*;
import static net.thucydides.core.steps.construction.StepLibraryType.ofTypePages;

/**
 * Produces an instance of a set of requirement steps for use in the acceptance tests.
 * Requirement steps navigate through pages using a WebDriver driver.
 */
public class StepFactory {

    private final Pages pages;

    private final Map<Class<?>, Object> index = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(StepFactory.class);
    private final DependencyInjectorService dependencyInjectorService;

    /**
     * Create a new step factory.
     * All web-testing step factories need a Pages object, which is passed to ScenarioSteps objects when they
     * are created.
     */
    public StepFactory(final Pages pages) {
        this.pages = pages;
        this.dependencyInjectorService = Injectors.getInjector().getInstance(DependencyInjectorService.class);
    }

    /**
     * Create a new step factory without webdriver support.
     * This is to be used for non-webtest acceptance tests.
     */
    public StepFactory() {
        this(null);
    }

    private static final Class<?>[] CONSTRUCTOR_ARG_TYPES = {Pages.class};

    /**
     * Returns a new ScenarioSteps instance, of the specified type.
     * This is actually a proxy that allows reporting and screenshots to
     * be performed at each step.
     *
     * @param scenarioStepsClass the scenario step class
     * @param <T>                the scenario step class type
     * @return the instrumented step library
     */
    public <T> T getStepLibraryFor(final Class<T> scenarioStepsClass) {
        if (isStepLibraryInstantiatedFor(scenarioStepsClass)) {
            return getManagedStepLibraryFor(scenarioStepsClass);
        } else {
            return getNewStepLibraryFor(scenarioStepsClass);
        }
    }

    public <T> T getNewStepLibraryFor(final Class<T> scenarioStepsClass) {
        try {
            return instantiateNewStepLibraryFor(scenarioStepsClass);
        } catch (RuntimeException stepCreationFailed) {
            throw new StepInitialisationException("Failed to create step library for " + scenarioStepsClass.getSimpleName() + ":" + stepCreationFailed.getMessage(), stepCreationFailed);
        }
    }

    public <T> T getUniqueStepLibraryFor(final Class<T> scenarioStepsClass) {
        return instantiateUniqueStepLibraryFor(scenarioStepsClass);
    }

    public <T> T getUniqueStepLibraryFor(final Class<T> scenarioStepsClass, Object... parameters) {
        return instantiateUniqueStepLibraryFor(scenarioStepsClass, parameters);
    }

    public void reset() {
        index.clear();
    }

    private boolean isStepLibraryInstantiatedFor(Class<?> scenarioStepsClass) {
        return index.containsKey(scenarioStepsClass);
    }


    @SuppressWarnings("unchecked")
    private <T> T getManagedStepLibraryFor(Class<T> scenarioStepsClass) {
        return (T) index.get(scenarioStepsClass);
    }

    /**
     * Create a new instance of a class containing test steps.
     * This method will instrument the class appropriately and inject any nested step libraries or
     * other dependencies.
     */
    public <T> T instantiateNewStepLibraryFor(Class<T> scenarioStepsClass) {
        StepInterceptor stepInterceptor = new StepInterceptor(scenarioStepsClass);
        return instantiateNewStepLibraryFor(scenarioStepsClass, stepInterceptor);
    }

    /**
     * Create a new instance of a class containing test steps using custom interceptors.
     */
    public <T> T instantiateNewStepLibraryFor(Class<T> scenarioStepsClass,
                                              MethodInterceptor interceptor) {
        T steps = createProxyStepLibrary(scenarioStepsClass, interceptor);

        indexStepLibrary(scenarioStepsClass, steps);

        instantiateAnyNestedStepLibrariesIn(steps, scenarioStepsClass);

        injectOtherDependenciesInto(steps);

        return steps;
    }

    private <T> void injectOtherDependenciesInto(T steps) {
        List<DependencyInjector> dependencyInjectors = dependencyInjectorService.findDependencyInjectors();
        dependencyInjectors.addAll(getDefaultDependencyInjectors());

        for (DependencyInjector dependencyInjector : dependencyInjectors) {
            dependencyInjector.injectDependenciesInto(steps);
        }
    }

    private ImmutableList<? extends DependencyInjector> getDefaultDependencyInjectors() {
        return (pages != null) ?
                ImmutableList.of(new PageObjectDependencyInjector(pages),
                        new EnvironmentDependencyInjector()) :
                ImmutableList.of(new EnvironmentDependencyInjector());
    }

    private <T> T instantiateUniqueStepLibraryFor(Class<T> scenarioStepsClass, Object... parameters) {
        StepInterceptor stepInterceptor = new StepInterceptor(scenarioStepsClass);
        T steps = createProxyStepLibrary(scenarioStepsClass, stepInterceptor, parameters);

        instantiateAnyNestedStepLibrariesIn(steps, scenarioStepsClass);

        injectOtherDependenciesInto(steps);

        return steps;
    }

    @SuppressWarnings("unchecked")
    private <T> T createProxyStepLibrary(Class<T> scenarioStepsClass,
                                         MethodInterceptor interceptor,
                                         Object... parameters) {
        Enhancer e = new Enhancer();
        e.setSuperclass(scenarioStepsClass);
        e.setCallback(interceptor);

        final ConstructionStrategy strategy = StepLibraryConstructionStrategy.forClass(scenarioStepsClass)
                .getStrategy();
        if (STEP_LIBRARY_WITH_WEBDRIVER.equals(strategy)) {
            return webEnabledStepLibrary(scenarioStepsClass, e);
        } else if (STEP_LIBRARY_WITH_PAGES.equals(strategy)) {
            return stepLibraryWithPages(scenarioStepsClass, e);
        } else if (CONSTRUCTOR_WITH_PARAMETERS.equals(strategy) && parameters.length > 0) {
            return immutableStepLibrary(scenarioStepsClass, e, parameters);
        } else {
            return (T) e.create();
        }
    }

    private <T> T immutableStepLibrary(Class<T> scenarioStepsClass, Enhancer e, Object[] parameters) {
        return (T) e.create(argumentTypesFrom(scenarioStepsClass, parameters), parameters);
    }

    private Class<?>[] argumentTypesFrom(Class<?> scenarioStepsClass, Object[] parameters) {
        for (Constructor<?> candidateConstructor : inOrderOfIncreasingParameters(scenarioStepsClass.getDeclaredConstructors())) {
            Class<?>[] parameterTypes = candidateConstructor.getParameterTypes();
            if (parametersMatchFor(parameters, parameterTypes)) {
                return parameterTypes;
            }
        }
        throw new IllegalArgumentException("Could not find a matching constructor for class " + scenarioStepsClass + "with parameters " + Arrays.toString(parameters));
    }

    private Constructor<?>[] inOrderOfIncreasingParameters(Constructor<?>[] declaredConstructors) {
        List<Constructor<?>> sortedConstructors = Lists.newArrayList(declaredConstructors);
        Collections.sort(sortedConstructors,
                new Comparator<Constructor<?>>() {
                    @Override
                    public int compare(Constructor<?> o1, Constructor<?> o2) {
                        return Integer.compare(o1.getParameterTypes().length, o2.getParameterTypes().length);
                    }
                });
        return sortedConstructors.toArray(new Constructor<?>[]{});
    }

    private boolean parametersMatchFor(Object[] parameters, Class<?>[] parameterTypes) {
        int parameterNumber = 0;
        if (parameters.length != parameterTypes.length) {
            return false;
        } else {
            for (Class<?> parameterType : parameterTypes) {

                if (parameterNumber >= parameterTypes.length) {
                    return false;
                }

                if (parameter(parameters[parameterNumber]).cannotBeAssignedTo(parameterType)) {
                    return false;
                }
                
                if ((parameters[parameterNumber] != null)
                        && (!ClassUtils.isAssignable(parameters[parameterNumber].getClass(), parameterType))) {
                    return false;
                }
                parameterNumber++;
            }
        }
        return true;
    }

    private ParameterAssignementChecker parameter(Object parameter) {
        return new ParameterAssignementChecker(parameter);
    }

    private Class<?> forTheClassOfParameter(Object parameter) {
        if (parameter == null) {
            return Object.class;
        }

        return parameter.getClass();
    }


    private <T> T webEnabledStepLibrary(final Class<T> scenarioStepsClass, final Enhancer e) {
        if (StepLibraryType.ofClass(scenarioStepsClass).hasAPagesConstructor()) {
            Object[] arguments = new Object[1];
            arguments[0] = pages;
            return (T) e.create(CONSTRUCTOR_ARG_TYPES, arguments);
        } else {
            T newStepLibrary = (T) e.create();
            return injectPagesInto(scenarioStepsClass, newStepLibrary);
        }
    }

    private <T> T stepLibraryWithPages(final Class<T> scenarioStepsClass, final Enhancer e) {
        T newStepLibrary = (T) e.create();
        return injectPagesInto(scenarioStepsClass, newStepLibrary);
    }

    static class PageInjector {
        private final Pages pages;

        public PageInjector(Pages pages) {
            this.pages = pages;
        }

        public <T> T injectPagesInto(final Class<T> stepLibraryClass, T newStepLibrary) {
            if (ScenarioSteps.class.isAssignableFrom(stepLibraryClass)) {
                ((ScenarioSteps) newStepLibrary).setPages(pages);
            } else if (StepLibraryType.ofClass(stepLibraryClass).hasAPagesField()) {
                ImmutableSet<Field> fields = copyOf(Fields.of(stepLibraryClass).allFields());
                Field pagesField = Iterables.find(fields, ofTypePages());
                pagesField.setAccessible(true);
                try {
                    pagesField.set(newStepLibrary, pages);
                } catch (IllegalAccessException e) {
                    LOGGER.error("Could not instantiate pages field for step library {}", newStepLibrary);
                }
            }
            return newStepLibrary;
        }

    }

    private <T> T injectPagesInto(final Class<T> stepLibraryClass, T newStepLibrary) {
        return new PageInjector(pages).injectPagesInto(stepLibraryClass, newStepLibrary);
    }


    private <T> void indexStepLibrary(Class<T> scenarioStepsClass, T steps) {
        index.put(scenarioStepsClass, steps);
    }

    private <T> void instantiateAnyNestedStepLibrariesIn(final T steps,
                                                         final Class<T> scenarioStepsClass) {
        StepAnnotations.injectNestedScenarioStepsInto(steps, this, scenarioStepsClass);
    }

    private class ParameterAssignementChecker {
        private static final boolean PARAMETER_CAN_BE_ASSIGNED = false;
        private final Object parameter;

        public ParameterAssignementChecker(Object parameter) {
            this.parameter = parameter;
        }

        public boolean cannotBeAssignedTo(Class<?> parameterType) {
            if (parameter == null) {
                return PARAMETER_CAN_BE_ASSIGNED;
            }

            return (!ClassUtils.isAssignable(parameter.getClass(), parameterType));
        }
    }
}