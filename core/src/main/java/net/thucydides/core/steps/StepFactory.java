package net.thucydides.core.steps;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.di.DependencyInjectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableSet.copyOf;

/**
 * Produces an instance of a set of requirement steps for use in the acceptance tests.
 * Requirement steps navigate through pages using a WebDriver driver.
 */
public class StepFactory {

    private final Pages pages;

    private final Map<Class<?>, Object> index = new HashMap<Class<?>, Object>();
    private static final Logger LOGGER = LoggerFactory.getLogger(StepFactory.class);
    private final DependencyInjectorService dependencyInjectorService;
    private boolean throwExceptionImmediately = false;

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
     * @param scenarioStepsClass the scenario step class
     * @param <T> the scenario step class type
     * @return the instrumented step library
     */
    public <T> T getStepLibraryFor(final Class<T> scenarioStepsClass) {
        if (isStepLibraryInstantiatedFor(scenarioStepsClass)) {
            return getManagedStepLibraryFor(scenarioStepsClass);
        } else {
            return instantiateNewStepLibraryFor(scenarioStepsClass);
        }
    }

    public <T> T getNewStepLibraryFor(final Class<T> scenarioStepsClass) {
        if (isStepLibraryInstantiatedFor(scenarioStepsClass)) {
            return getManagedStepLibraryFor(scenarioStepsClass);
        } else {
            return instantiateNewStepLibraryFor(scenarioStepsClass);
        }
    }

    public <T> T getUniqueStepLibraryFor(final Class<T> scenarioStepsClass) {
        return instantiateUniqueStepLibraryFor(scenarioStepsClass);
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
        stepInterceptor.setThowsExceptionImmediately(throwExceptionImmediately);
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

        for(DependencyInjector dependencyInjector : dependencyInjectors) {
            dependencyInjector.injectDependenciesInto(steps);
        }
    }

    private List<DependencyInjector> getDefaultDependencyInjectors() {
        return ImmutableList.of((DependencyInjector)new PageObjectDependencyInjector(pages));
    }

    private <T> T instantiateUniqueStepLibraryFor(Class<T> scenarioStepsClass) {
        StepInterceptor stepInterceptor = new StepInterceptor(scenarioStepsClass);
        stepInterceptor.setThowsExceptionImmediately(throwExceptionImmediately);
        T steps = createProxyStepLibrary(scenarioStepsClass, stepInterceptor);

        instantiateAnyNestedStepLibrariesIn(steps, scenarioStepsClass);
        
        injectOtherDependenciesInto(steps);

        return steps;
    }

    @SuppressWarnings("unchecked")
    private <T> T createProxyStepLibrary(Class<T> scenarioStepsClass,
                                         MethodInterceptor interceptor) {
        Enhancer e = new Enhancer();
        e.setSuperclass(scenarioStepsClass);
        e.setCallback(interceptor);

        if (isWebdriverStepClass(scenarioStepsClass)) {
            return webEnabledStepLibrary(scenarioStepsClass, e);
        } else {
            return (T) e.create();
        }
    }

    private <T> T webEnabledStepLibrary(final Class<T> scenarioStepsClass, final Enhancer e) {
        if (hasAPagesConstructor(scenarioStepsClass)) {
            Object[] arguments = new Object[1];
            arguments[0] = pages;
            return (T) e.create(CONSTRUCTOR_ARG_TYPES, arguments);
        } else {
            T newStepLibrary = (T) e.create();
            return injectPagesInto(scenarioStepsClass, newStepLibrary);
        }
    }

    private <T> T injectPagesInto(final Class<T> stepLibraryClass, T newStepLibrary) {
        if (ScenarioSteps.class.isAssignableFrom(stepLibraryClass))  {
            ((ScenarioSteps) newStepLibrary).setPages(pages);
        } else if (hasAPagesField(stepLibraryClass)) {
            ImmutableSet<Field> fields = copyOf(stepLibraryClass.getDeclaredFields());
            Field pagesField =  Iterables.find(fields, ofTypePages());
            pagesField.setAccessible(true);
            try {
                pagesField.set(newStepLibrary, pages);
            } catch (IllegalAccessException e) {
                LOGGER.error("Could not instantiate pages field for step library {}", newStepLibrary);
            }
        }
        return newStepLibrary;
    }

    private <T> boolean isWebdriverStepClass(final Class<T> stepLibraryClass) {

        return (isAScenarioStepClass(stepLibraryClass)
                || hasAPagesConstructor(stepLibraryClass)
                || hasAPagesField(stepLibraryClass));
    }

    private <T> boolean hasAPagesConstructor(final Class<T> stepLibraryClass) {
        ImmutableSet<Constructor<?>> constructors = copyOf(stepLibraryClass.getDeclaredConstructors());
        return Iterables.any(constructors, withASinglePagesParameter());

    }

    private <T> boolean hasAPagesField(final Class<T> stepLibraryClass) {
        ImmutableSet<Field> fields = copyOf(stepLibraryClass.getDeclaredFields());
        return Iterables.any(fields, ofTypePages());

    }

    private Predicate<Constructor> withASinglePagesParameter() {
        return new Predicate<Constructor>() {

            public boolean apply(Constructor constructor) {
                return ((constructor.getParameterTypes().length == 1)
                        && (constructor.getParameterTypes()[0] == Pages.class));
            }
        };
    }

    private Predicate<Field> ofTypePages() {
        return new Predicate<Field>() {
            public boolean apply(Field field) {
                return (field.getType() == Pages.class);
            }
        };
    }

    private <T> boolean isAScenarioStepClass(final Class<T> stepLibraryClass) {
        return ScenarioSteps.class.isAssignableFrom(stepLibraryClass);
    }

    private <T> void indexStepLibrary(Class<T> scenarioStepsClass, T steps) {
        index.put(scenarioStepsClass, steps);
    }

    private <T> void instantiateAnyNestedStepLibrariesIn(final T steps,
                                                         final Class<T> scenarioStepsClass) {
        StepAnnotations.injectNestedScenarioStepsInto(steps, this, scenarioStepsClass);
    }

    public StepFactory thatThrowsExcpetionsImmediately() {
        throwExceptionImmediately = true;
        return this;
    }
}