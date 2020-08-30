package net.thucydides.core.steps;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.collect.NewSet;
import net.serenitybdd.core.di.DependencyInjector;
import net.serenitybdd.core.exceptions.StepInitialisationException;
import net.serenitybdd.core.injectors.EnvironmentDependencyInjector;
import net.serenitybdd.core.lifecycle.LifecycleRegister;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.construction.ConstructionStrategy;
import net.thucydides.core.steps.construction.StepLibraryConstructionStrategy;
import net.thucydides.core.steps.construction.StepLibraryType;
import net.thucydides.core.steps.di.DependencyInjectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.not;
import static net.thucydides.core.steps.construction.ConstructionStrategy.*;
import static net.thucydides.core.steps.construction.StepLibraryType.ofTypePages;

/**
 * Produces an instance of a set of requirement steps for use in the acceptance tests.
 * Requirement steps navigate through pages using a WebDriver driver.
 */
public class StepFactory {

    private static final boolean WITH_NO_CACHING = false;
    private static final boolean WITH_CACHING = true;
    private Pages pages;

    private final Map<Class<?>, Object> index = new HashMap();
    private static final Logger LOGGER = LoggerFactory.getLogger(StepFactory.class);
    private final DependencyInjectorService dependencyInjectorService;

    private static ThreadLocal<StepFactory> currentStepFactory = ThreadLocal.withInitial(() -> new StepFactory());
    private Method privateLookupIn;
    private Object lookup;

    private final ByteBuddy byteBuddy;
    private TypeCache<TypeCache.SimpleKey> proxyCache;

    /**
     * Create a new step factory.
     * All web-testing step factories need a Pages object, which is passed to ScenarioSteps objects when they
     * are created.
     */
    public StepFactory(final Pages pages) {
        this.byteBuddy = new ByteBuddy().with( TypeValidation.DISABLED );
        this.pages = pages;
        this.dependencyInjectorService = Injectors.getInjector().getInstance(DependencyInjectorService.class);
        this.proxyCache = new TypeCache.WithInlineExpunction<TypeCache.SimpleKey>( TypeCache.Sort.WEAK );
        try {
            if (ClassInjector.UsingLookup.isAvailable()) {
                Class<?> methodHandles = Class.forName("java.lang.invoke.MethodHandles");
                lookup = methodHandles.getMethod("lookup").invoke(null);
                privateLookupIn = methodHandles.getMethod("privateLookupIn",
                        Class.class,
                        Class.forName("java.lang.invoke.MethodHandles$Lookup"));
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("Cannot get privateLookupIn method ClassInjector using lookup", e);
            throw new IllegalStateException("No code generation strategy available");
        }
    }

    /**
     * Create a new step factory without webdriver support.
     * This is to be used for non-webtest acceptance tests.
     */
    public StepFactory() {
        this(null);
    }

    public static StepFactory getFactory() {
        return currentStepFactory.get();
    }

    public StepFactory usingPages(Pages pages) {
        this.pages = pages;
        return this;
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
    public <T> T getSharedStepLibraryFor(final Class<T> scenarioStepsClass) {
        if (isStepLibraryInstantiatedFor(scenarioStepsClass)) {
            return getStepLibraryFromCacheFor(scenarioStepsClass);
        } else {
            return getNewCachedStepLibraryFor(scenarioStepsClass);
        }
    }

    public <T> T getNewStepLibraryFor(final Class<T> scenarioStepsClass) {
        try {
            return instantiateNewStepLibraryFor(scenarioStepsClass, WITH_NO_CACHING);
        } catch (RecursiveOrCyclicStepLibraryReferenceException recurciveCallException) {
            throw recurciveCallException;
        } catch (RuntimeException stepCreationFailed) {
            throw new StepInitialisationException("Failed to create step library for "
                    + scenarioStepsClass.getSimpleName()
                    + ":" + stepCreationFailed.getMessage(),
                    stepCreationFailed);
        }
    }

    public <T> T getNewCachedStepLibraryFor(final Class<T> scenarioStepsClass) {
        try {
            return instantiateNewStepLibraryFor(scenarioStepsClass, WITH_CACHING);
        } catch (RecursiveOrCyclicStepLibraryReferenceException recursiveCallException) {
            throw recursiveCallException;
        } catch (RuntimeException stepCreationFailed) {
            throw new StepInitialisationException("Failed to create step library for "
                    + scenarioStepsClass.getSimpleName()
                    + ":" + stepCreationFailed.getMessage(),
                    stepCreationFailed);
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
    private <T> T getStepLibraryFromCacheFor(Class<T> scenarioStepsClass) {
        return (T) index.get(scenarioStepsClass);
    }

    /**
     * Create a new instance of a class containing test steps.
     * This method will instrument the class appropriately and inject any nested step libraries or
     * other dependencies.
     */
    public <T> T instantiateNewStepLibraryFor(Class<T> scenarioStepsClass, boolean cacheNewInstance) {
        StepInterceptor stepInterceptor = new StepInterceptor(scenarioStepsClass);
        return instantiateNewStepLibraryFor(scenarioStepsClass, stepInterceptor, cacheNewInstance);
    }

    /**
     * Create a new instance of a class containing test steps using custom interceptors.
     */
    public <T> T instantiateNewStepLibraryFor(Class<T> scenarioStepsClass,
                                              Interceptor interceptor,
                                              boolean useCache) {
        T steps = createProxyStepLibrary(scenarioStepsClass, interceptor);

        LifecycleRegister.register(steps);

        if (useCache) {
            indexStepLibrary(scenarioStepsClass, steps);
        }

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

    private List<? extends DependencyInjector> getDefaultDependencyInjectors() {
        return (pages != null) ?
                NewList.of(new PageObjectDependencyInjector(pages),
                        new EnvironmentDependencyInjector()) :
                NewList.of(new EnvironmentDependencyInjector());
    }

    private <T> T instantiateUniqueStepLibraryFor(Class<T> scenarioStepsClass, Object... parameters) {
        StepInterceptor stepInterceptor = new StepInterceptor(scenarioStepsClass);
        T steps = createProxyStepLibrary(scenarioStepsClass, stepInterceptor, parameters);

        LifecycleRegister.register(steps);

        instantiateAnyNestedStepLibrariesIn(steps, scenarioStepsClass);

        injectOtherDependenciesInto(steps);

        return steps;
    }

    @SuppressWarnings("unchecked")
    private <T> T createProxyStepLibrary(Class<T> scenarioStepsClass,
                                         Interceptor interceptor,
                                         Object... parameters) {
        final TypeCache.SimpleKey cacheKey = getCacheKey( scenarioStepsClass, interceptor.getClass() );

        Class proxyClass = load(scenarioStepsClass,proxyCache,cacheKey,byteBuddy->byteBuddy.subclass(scenarioStepsClass)
                .defineField( ProxyConfiguration.INTERCEPTOR_FIELD_NAME, Interceptor.class, Visibility.PRIVATE )
                .method(not(isDeclaredBy(Object.class)))
                .intercept( MethodDelegation.to( ProxyConfiguration.InterceptorDispatcher.class ))
                .implement( ProxyConfiguration.class )
                .intercept( FieldAccessor.ofField( ProxyConfiguration.INTERCEPTOR_FIELD_NAME ).withAssigner( Assigner.DEFAULT, Assigner.Typing.DYNAMIC )));

        try {
            final ConstructionStrategy strategy = StepLibraryConstructionStrategy.forClass(scenarioStepsClass)
                    .getStrategy();
            if (STEP_LIBRARY_WITH_WEBDRIVER.equals(strategy)) {
                return webEnabledStepLibrary(scenarioStepsClass, proxyClass,interceptor);
            } else if (STEP_LIBRARY_WITH_PAGES.equals(strategy)) {
                return stepLibraryWithPages(scenarioStepsClass, proxyClass,interceptor);
            } else if (CONSTRUCTOR_WITH_PARAMETERS.equals(strategy) && parameters.length > 0) {
                return immutableStepLibrary(scenarioStepsClass, proxyClass, parameters,interceptor);
            } else if (INNER_CLASS_CONSTRUCTOR.equals(strategy)) {
                return immutableStepLibrary(scenarioStepsClass, proxyClass, EnclosingClass.of(scenarioStepsClass).asParameters(),interceptor);
            } else {
                final ProxyConfiguration proxy = (ProxyConfiguration)proxyClass.getDeclaredConstructor().newInstance();
                proxy.$$_serenity_set_interceptor(interceptor);
                return (T) proxy;
            }
        } catch (NoSuchMethodException|IllegalAccessException|InvocationTargetException|InstantiationException ex) {
            LOGGER.error("Cannot create StepFactory for  " + scenarioStepsClass , ex);
            return null;
        }
    }

    private Class<?> load(Class<?> referenceClass, TypeCache<TypeCache.SimpleKey> cache,
                          TypeCache.SimpleKey cacheKey, Function<ByteBuddy, DynamicType.Builder<?>> makeProxyFunction) {
        return cache.findOrInsert(
                referenceClass.getClassLoader(),
                cacheKey,
                () -> makeProxyFunction.apply(byteBuddy).make()
                        .load(referenceClass.getClassLoader(), getClassLoadingStrategy(referenceClass))
                        .getLoaded(),
                cache );
    }

    private ClassLoadingStrategy getClassLoadingStrategy(Class targetClass) {
        ClassLoadingStrategy<ClassLoader> strategy;
        try {
            if (ClassInjector.UsingLookup.isAvailable()) {
                Object privateLookup = privateLookupIn.invoke(null, targetClass, lookup);
                strategy = ClassLoadingStrategy.UsingLookup.of(privateLookup);
            } else if (ClassInjector.UsingReflection.isAvailable()) {
                strategy = ClassLoadingStrategy.Default.INJECTION;
            } else {
                throw new IllegalStateException("No code generation strategy available");
            }
            return strategy;
        } catch (InvocationTargetException | IllegalAccessException  e) {
            LOGGER.error("Cannot get ClassLoadingStrategy  for target class " +  targetClass , e);
            throw new IllegalStateException("No code generation strategy available");
        }
    }

    private <T> T immutableStepLibrary(Class<T> scenarioStepsClass, Class proxyClass, Object[] parameters,Interceptor interceptor) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        final ProxyConfiguration proxy = (ProxyConfiguration)proxyClass.getDeclaredConstructor(argumentTypesFrom(scenarioStepsClass,parameters)).newInstance(parameters);
        proxy.$$_serenity_set_interceptor(interceptor);
        return (T) proxy;
    }

    private Class<?>[] argumentTypesFrom(Class<?> scenarioStepsClass, Object[] parameters) {
        for (Constructor<?> candidateConstructor : inOrderOfIncreasingParameters(scenarioStepsClass.getDeclaredConstructors())) {
            Class<?>[] parameterTypes = candidateConstructor.getParameterTypes();
            if (parametersMatchFor(parameters, parameterTypes)) {
                return parameterTypes;
            }
        }
        throw new IllegalArgumentException("Could not find a matching constructor for class " + scenarioStepsClass + " with parameters " + Arrays.toString(parameters));
    }

    private Constructor<?>[] inOrderOfIncreasingParameters(Constructor<?>[] declaredConstructors) {
        List<Constructor<?>> sortedConstructors = NewList.of(declaredConstructors);
        Collections.sort(sortedConstructors, Comparator.comparingInt(o -> o.getParameterTypes().length));
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
                        && (!isAssignableFrom(parameters[parameterNumber], parameterType))) {
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

    private <T> T webEnabledStepLibrary(final Class<T> scenarioStepsClass, final Class proxyClass,Interceptor interceptor)throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        if (StepLibraryType.ofClass(scenarioStepsClass).hasAPagesConstructor()) {
            Object[] arguments = new Object[1];
            arguments[0] = pages;
            final ProxyConfiguration proxy = (ProxyConfiguration)proxyClass.getDeclaredConstructor(CONSTRUCTOR_ARG_TYPES).newInstance(arguments);
            proxy.$$_serenity_set_interceptor(interceptor);
            return (T) proxy;
        } else {
            final ProxyConfiguration newStepLibrary = (ProxyConfiguration)proxyClass.newInstance();
            newStepLibrary.$$_serenity_set_interceptor(interceptor);
            return injectPagesInto(scenarioStepsClass, (T)newStepLibrary);
        }
    }

    private <T> T stepLibraryWithPages(final Class<T> scenarioStepsClass, final Class proxyClass, final Interceptor interceptor) throws IllegalAccessException, InstantiationException {
        final ProxyConfiguration newStepLibrary = (ProxyConfiguration)(T) proxyClass.newInstance();;
        newStepLibrary.$$_serenity_set_interceptor(interceptor);
        return injectPagesInto(scenarioStepsClass, (T)newStepLibrary);
    }

    public void usePageFactory(Pages pages) {
        this.pages = pages;
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
                Set<Field> fields = NewSet.copyOf(Fields.of(stepLibraryClass).allFields());
                Field pagesField = fields.stream().filter(ofTypePages()).findFirst().get();
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
        StepAnnotations.injector().injectNestedScenarioStepsInto(steps, this, scenarioStepsClass);
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
            return (!isAssignableFrom(parameter, parameterType));
        }
    }

    public static boolean isAssignableFrom(final Object argument, final Class<?> parameterType) {

        if (parameterType.isInterface()) {
            return parameterType.isInstance(argument);
        }

        Class<?> argumentType = argument.getClass();

        if (isByte(parameterType)) {
            return isByte(argumentType);
        } else if (isShort(parameterType)) {
            return isShort(argumentType) || isByte(argumentType);
        } else if (isInteger(parameterType)) {
            return isInteger(argumentType) || isShort(argumentType) || isByte(argumentType);
        } else if (isLong(parameterType)) {
            return isLong(argumentType) || isInteger(argumentType) || isShort(argumentType) || isByte(argumentType);
        } else if (isFloat(parameterType)) {
            return isFloat(argumentType);
        } else if (isDouble(parameterType)) {
            return isDouble(argumentType) || isFloat(argumentType);
        } else if (isBoolean(parameterType)) {
            return isBoolean(argumentType);
        } else if (isCharacter(parameterType)) {
            return isCharacter(argumentType);
        }
        return parameterType.isAssignableFrom(argumentType);
    }

    private static boolean isInteger(Class<?> fieldType) {
        return (fieldType.equals(Integer.class) || fieldType.equals(int.class));
    }

    private static boolean isFloat(Class<?> fieldType) {
        return (fieldType.equals(Float.class) || fieldType.equals(float.class));
    }

    private static boolean isDouble(Class<?> fieldType) {
        return (fieldType.equals(Double.class) || fieldType.equals(double.class));
    }

    private static boolean isCharacter(Class<?> fieldType) {
        return (fieldType.equals(Character.class) || fieldType.equals(char.class));
    }

    private static boolean isLong(Class<?> fieldType) {
        return (fieldType.equals(Long.class) || fieldType.equals(long.class));
    }

    private static boolean isShort(Class<?> fieldType) {
        return (fieldType.equals(Short.class) || fieldType.equals(short.class));
    }

    private static boolean isBoolean(Class<?> fieldType) {
        return (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class));
    }

    private static boolean isByte(Class<?> fieldType) {
        return (fieldType.equals(Byte.class) || fieldType.equals(byte.class));
    }

    private TypeCache.SimpleKey getCacheKey(Class<?> scenarioStepsClass, Class<?> interceptorClass) {
        Set<Class<?>> key = new HashSet<Class<?>>();
        if ( scenarioStepsClass != null ) {
            key.add( scenarioStepsClass );
        }
        if ( interceptorClass != null ) {
            key.add ( interceptorClass );
        }
        return new TypeCache.SimpleKey( key );
    }
}