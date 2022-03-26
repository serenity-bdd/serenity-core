package io.cucumber.junit;

import io.cucumber.core.eventbus.EventBus;
import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.filter.Filters;
import io.cucumber.core.gherkin.Feature;
import io.cucumber.core.gherkin.Pickle;
import io.cucumber.core.options.*;
import io.cucumber.core.plugin.PluginFactory;
import io.cucumber.core.plugin.Plugins;
import io.cucumber.core.plugin.SerenityReporter;
import io.cucumber.core.resource.ClassLoaders;
import io.cucumber.core.runtime.Runtime;
import io.cucumber.core.runtime.*;
import io.cucumber.plugin.Plugin;
import io.cucumber.tagexpressions.Expression;
import net.serenitybdd.cucumber.SerenityOptions;
import net.serenitybdd.cucumber.suiteslicing.CucumberSuiteSlicer;
import net.serenitybdd.cucumber.suiteslicing.ScenarioFilter;
import net.serenitybdd.cucumber.suiteslicing.TestStatistics;
import net.serenitybdd.cucumber.suiteslicing.WeightedCucumberScenarios;
import net.serenitybdd.cucumber.util.PathUtils;
import net.serenitybdd.cucumber.util.Splitter;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import org.junit.runner.Description;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Clock;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.cucumber.core.runtime.SynchronizedEventBus.synchronize;
import static io.cucumber.junit.FileNameCompatibleNames.uniqueSuffix;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static net.thucydides.core.ThucydidesSystemProperty.*;

/**
 * Glue code for running Cucumber via Serenity.
 * Sets up Serenity reporting and instrumentation.
 */
public class CucumberSerenityRunner extends ParentRunner<ParentRunner<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CucumberSerenityRunner.class);

    private List<ParentRunner<?>> children;
    private final EventBus bus;
    private static ThreadLocal<RuntimeOptions> RUNTIME_OPTIONS = new ThreadLocal<>();

    private final List<Feature> features;
    private final Plugins plugins;
    private final CucumberExecutionContext context;

    private boolean multiThreadingAssumed = false;

    /**
     * Constructor called by JUnit.
     *
     * @param clazz the class with the @RunWith annotation.
     * @throws InitializationError if there is another problem
     */
    public CucumberSerenityRunner(Class clazz) throws InitializationError {
        super(clazz);
        Assertions.assertNoCucumberAnnotatedMethods(clazz);

        // Parse the options early to provide fast feedback about invalid options
        RuntimeOptions propertiesFileOptions = new CucumberPropertiesParser()
                .parse(CucumberProperties.fromPropertiesFile())
                .build();

        RuntimeOptions annotationOptions = new CucumberOptionsAnnotationParser()
                .withOptionsProvider(new JUnitCucumberOptionsProvider())
                .parse(clazz)
                .build(propertiesFileOptions);

        RuntimeOptions environmentOptions = new CucumberPropertiesParser()
                .parse(CucumberProperties.fromEnvironment())
                .build(annotationOptions);

        RuntimeOptions runtimeOptions = new CucumberPropertiesParser()
                .parse(CucumberProperties.fromSystemProperties())
                .enablePublishPlugin()
                .build(environmentOptions);

        RuntimeOptionsBuilder runtimeOptionsBuilder = new RuntimeOptionsBuilder();
        Collection<String> tagFilters = environmentSpecifiedTags(runtimeOptions.getTagExpressions());
        for (String tagFilter : tagFilters) {
            runtimeOptionsBuilder.addTagFilter(new LiteralExpression(tagFilter));
        }
        runtimeOptionsBuilder.build(runtimeOptions);

        // Next parse the junit options
        JUnitOptions junitPropertiesFileOptions = new JUnitOptionsParser()
                .parse(CucumberProperties.fromPropertiesFile())
                .build();

        JUnitOptions junitAnnotationOptions = new JUnitOptionsParser()
                .parse(clazz)
                .build(junitPropertiesFileOptions);

        JUnitOptions junitEnvironmentOptions = new JUnitOptionsParser()
                .parse(CucumberProperties.fromEnvironment())
                .build(junitAnnotationOptions);

        JUnitOptions junitOptions = new JUnitOptionsParser()
                .parse(fromSystemPropertiesAndOptionsAnnotationIn(clazz))
                .build(junitEnvironmentOptions);

        this.bus = synchronize(new TimeServiceEventBus(Clock.systemUTC(), UUID::randomUUID));

        setRuntimeOptions(runtimeOptions);

        // Parse the features early. Don't proceed when there are lexer errors
        FeatureParser parser = new FeatureParser(bus::generateId);
        Supplier<ClassLoader> classLoader = ClassLoaders::getDefaultClassLoader;
        FeaturePathFeatureSupplier featureSupplier = new FeaturePathFeatureSupplier(classLoader, runtimeOptions, parser);
        this.features = featureSupplier.get();

        // Create plugins after feature parsing to avoid the creation of empty files on lexer errors.
        this.plugins = new Plugins(new PluginFactory(), runtimeOptions);
        ExitStatus exitStatus = new ExitStatus(runtimeOptions);
        this.plugins.addPlugin(exitStatus);

        ObjectFactoryServiceLoader objectFactoryServiceLoader = new ObjectFactoryServiceLoader(classLoader,runtimeOptions);
        ObjectFactorySupplier objectFactorySupplier = new ThreadLocalObjectFactorySupplier(objectFactoryServiceLoader);
        BackendSupplier backendSupplier = new BackendServiceLoader(clazz::getClassLoader, objectFactorySupplier);
        ThreadLocalRunnerSupplier runnerSupplier = new ThreadLocalRunnerSupplier(runtimeOptions, bus, backendSupplier, objectFactorySupplier);

        Configuration systemConfiguration = Injectors.getInjector().getInstance(Configuration.class);
        SerenityReporter reporter = new SerenityReporter(systemConfiguration);
        addSerenityReporterPlugin(plugins, reporter);

        this.context = new CucumberExecutionContext(bus, exitStatus, runnerSupplier);
        Predicate<Pickle> filters = new Filters(runtimeOptions);

        Map<Optional<String>, List<Feature>> groupedByName = features.stream()
                .collect(groupingBy(Feature::getName));

        this.children = features.stream()
                .map(feature -> {
                    Integer uniqueSuffix = uniqueSuffix(groupedByName, feature, Feature::getName);
                    return FeatureRunner.create(feature, uniqueSuffix, filters, context, junitOptions);
                })
                .filter(runner -> !runner.isEmpty())
                .collect(toList());
    }

    private Map<String,String> fromSystemPropertiesAndOptionsAnnotationIn(Class clazz) {
        if (clazz.getAnnotation(SerenityOptions.class) == null) {
            return CucumberProperties.fromSystemProperties();
        } else {
            Map<String, String> systemProperties = new HashMap<>(CucumberProperties.fromSystemProperties());
            SerenityOptions options = (SerenityOptions) clazz.getAnnotation(SerenityOptions.class);
            stream(options.value().split(",")).forEach(
                    option -> {
                        String[] optionParts = option.split("=");
                        String key = optionParts[0].trim();
                        String value = (optionParts.length == 1) ? "true" : optionParts[1].trim();
                        systemProperties.put(key,value);
                    }
            );
            return systemProperties;
        }
    }



    private static RuntimeOptions DEFAULT_RUNTIME_OPTIONS;

    public static void setRuntimeOptions(RuntimeOptions runtimeOptions) {
        RUNTIME_OPTIONS.set(runtimeOptions);
        DEFAULT_RUNTIME_OPTIONS = runtimeOptions;
    }

    public static RuntimeOptions currentRuntimeOptions() {
        return (RUNTIME_OPTIONS.get() != null) ? RUNTIME_OPTIONS.get() : DEFAULT_RUNTIME_OPTIONS;
    }

    private static Collection<String> environmentSpecifiedTags(List<?> existingTags) {
        EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
        String tagsExpression = ThucydidesSystemProperty.TAGS.from(environmentVariables, "");
        List<String> existingTagsValues = existingTags.stream().map(Object::toString).collect(toList());
        return Splitter.on(",").trimResults().omitEmptyStrings().splitToList(tagsExpression).stream()
                .map(CucumberSerenityRunner::toCucumberTag).filter(t -> !existingTagsValues.contains(t)).collect(toList());
    }

    private static String toCucumberTag(String from) {
        String tag = from.replaceAll(":", "=");
        if (tag.startsWith("~@") || tag.startsWith("@")) {
            return tag;
        }
        if (tag.startsWith("~")) {
            return "~@" + tag.substring(1);
        }

        return "@" + tag;
    }

    public static Runtime createSerenityEnabledRuntime(/*ResourceLoader resourceLoader,*/
            Supplier<ClassLoader> classLoaderSupplier,
            RuntimeOptions runtimeOptions,
            Configuration systemConfiguration) {
        RuntimeOptionsBuilder runtimeOptionsBuilder = new RuntimeOptionsBuilder();
        Collection<String> allTagFilters = environmentSpecifiedTags(runtimeOptions.getTagExpressions());
        for (String tagFilter : allTagFilters) {
            runtimeOptionsBuilder.addTagFilter(new LiteralExpression(tagFilter));
        }
        runtimeOptionsBuilder.build(runtimeOptions);
        setRuntimeOptions(runtimeOptions);


        EventBus bus = new TimeServiceEventBus(Clock.systemUTC(), UUID::randomUUID);
        FeatureParser parser = new FeatureParser(bus::generateId);
        FeaturePathFeatureSupplier featureSupplier = new FeaturePathFeatureSupplier(classLoaderSupplier, runtimeOptions, parser);

        SerenityReporter serenityReporter = new SerenityReporter(systemConfiguration);
        Runtime runtime = Runtime.builder().withClassLoader(classLoaderSupplier).withRuntimeOptions(runtimeOptions).
                withAdditionalPlugins(serenityReporter).
                withEventBus(bus).withFeatureSupplier(featureSupplier).
                build();

        return runtime;
    }

    private static void addSerenityReporterPlugin(Plugins plugins, SerenityReporter plugin) {
        for (Plugin currentPlugin : plugins.getPlugins()) {
            if (currentPlugin instanceof SerenityReporter) {
                return;
            }
        }
        plugins.addPlugin(plugin);
    }


    @Override
    protected Description describeChild(ParentRunner<?> child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(ParentRunner<?> child, RunNotifier notifier) {
        child.run(notifier);
    }

    @Override
    protected Statement childrenInvoker(RunNotifier notifier) {
        Statement runFeatures = super.childrenInvoker(notifier);
        return new RunCucumber(runFeatures);
    }

    class RunCucumber extends Statement {
        private final Statement runFeatures;

        RunCucumber(Statement runFeatures) {
            this.runFeatures = runFeatures;
        }

        @Override
        public void evaluate() throws Throwable {
            if (multiThreadingAssumed) {
                plugins.setSerialEventBusOnEventListenerPlugins(bus);
            } else {
                plugins.setEventBusOnEventListenerPlugins(bus);
            }

            context.startTestRun();
            context.runBeforeAllHooks();
            features.forEach(context::beforeFeature);

            try {
                runFeatures.evaluate();
            } finally {
                context.runAfterAllHooks();
                context.finishTestRun();
                StepEventBus.getEventBus().testRunFinished();
            }
        }
    }

    @Override
    public void setScheduler(RunnerScheduler scheduler) {
        super.setScheduler(scheduler);
        multiThreadingAssumed = true;
    }


    @Override
    public List<ParentRunner<?>> getChildren() {
        try {
            EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
            RuntimeOptions runtimeOptions = currentRuntimeOptions();
            List<Expression> tagFilters = runtimeOptions.getTagExpressions();
            List<URI> featurePaths = runtimeOptions.getFeaturePaths();
            int batchNumber = environmentVariables.getPropertyAsInteger(SERENITY_BATCH_NUMBER, 1);
            int batchCount = environmentVariables.getPropertyAsInteger(SERENITY_BATCH_COUNT, 1);
            int forkNumber = environmentVariables.getPropertyAsInteger(SERENITY_FORK_NUMBER, 1);
            int forkCount = environmentVariables.getPropertyAsInteger(SERENITY_FORK_COUNT, 1);
            if ((batchCount == 1) && (forkCount == 1)) {
                return children;
            } else {
                LOGGER.info("Running slice {} of {} using fork {} of {} from feature paths {}", batchNumber, batchCount, forkNumber, forkCount, featurePaths);

                List<String> tagFiltersAsString = tagFilters.stream().map(Expression::toString).collect(toList());
                WeightedCucumberScenarios weightedCucumberScenarios = new CucumberSuiteSlicer(featurePaths, TestStatistics.from(environmentVariables, featurePaths))
                        .scenarios(batchNumber, batchCount, forkNumber, forkCount, tagFiltersAsString);

                List<ParentRunner<?>> unfilteredChildren = children;
                AtomicInteger filteredInScenarioCount = new AtomicInteger();
                List<ParentRunner<?>> filteredChildren = unfilteredChildren.stream()
                        .filter(forIncludedFeatures(weightedCucumberScenarios))
                        .map(toPossibleFeatureRunner(weightedCucumberScenarios, filteredInScenarioCount))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(toList());

                if (filteredInScenarioCount.get() != weightedCucumberScenarios.totalScenarioCount()) {
                    LOGGER.warn(
                            "There is a mismatch between the number of scenarios included in this test run ({}) and the expected number of scenarios loaded ({}). This suggests that the scenario filtering is not working correctly or feature file(s) of an unexpected structure are being run",
                            filteredInScenarioCount.get(),
                            weightedCucumberScenarios.scenarios.size());
                }

                LOGGER.info("Running {} of {} features", filteredChildren.size(), unfilteredChildren.size());
                return filteredChildren;
            }
        } catch (Exception e) {
            LOGGER.error("Test failed to start", e);
            throw e;
        }
    }

    private Function<ParentRunner<?>, Optional<ParentRunner<?>>> toPossibleFeatureRunner(WeightedCucumberScenarios weightedCucumberScenarios, AtomicInteger filteredInScenarioCount) {
        return featureRunner -> {
            int initialScenarioCount = featureRunner.getDescription().getChildren().size();
            String featureName = FeatureRunnerExtractors.extractFeatureName(featureRunner);
            try {
                ScenarioFilter filter = weightedCucumberScenarios.createFilterContainingScenariosIn(featureName);
                String featurePath = FeatureRunnerExtractors.featurePathFor(featureRunner);
                featureRunner.filter(filter);
                if (!filter.scenariosIncluded().isEmpty()) {
                    LOGGER.info("{} scenario(s) included for '{}' in {}", filter.scenariosIncluded().size(), featureName, featurePath);
                    filter.scenariosIncluded().forEach(scenario -> {
                        LOGGER.info("Included scenario '{}'", scenario);
                        filteredInScenarioCount.getAndIncrement();
                    });
                }
                if (!filter.scenariosExcluded().isEmpty()) {
                    LOGGER.debug("{} scenario(s) excluded for '{}' in {}", filter.scenariosExcluded().size(), featureName, featurePath);
                    filter.scenariosExcluded().forEach(scenario -> LOGGER.debug("Excluded scenario '{}'", scenario));
                }
                return Optional.of(featureRunner);
            } catch (NoTestsRemainException e) {
                LOGGER.info("Filtered out all {} scenarios for feature '{}'", initialScenarioCount, featureName);
                return Optional.empty();
            }
        };
    }

    private Predicate<ParentRunner<?>> forIncludedFeatures(WeightedCucumberScenarios weightedCucumberScenarios) {
        return featureRunner -> {
            String featureName = FeatureRunnerExtractors.extractFeatureName(featureRunner);
            String featurePath = PathUtils.getAsFile(FeatureRunnerExtractors.featurePathFor(featureRunner)).getName();
            boolean matches = weightedCucumberScenarios.scenarios.stream().anyMatch(scenario -> featurePath.equals(scenario.featurePath));
            LOGGER.debug("{} in filtering '{}' in {}", matches ? "Including" : "Not including", featureName, featurePath);
            return matches;
        };
    }

}
