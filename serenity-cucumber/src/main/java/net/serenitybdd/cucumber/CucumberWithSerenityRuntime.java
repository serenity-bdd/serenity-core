package net.serenitybdd.cucumber;


import io.cucumber.core.eventbus.EventBus;
import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.options.CucumberProperties;
import io.cucumber.core.options.CucumberPropertiesParser;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.core.options.RuntimeOptionsBuilder;
import io.cucumber.core.plugin.SerenityReporter;
import io.cucumber.core.runtime.FeaturePathFeatureSupplier;
import io.cucumber.core.runtime.Runtime;
import io.cucumber.core.runtime.TimeServiceEventBus;
import io.cucumber.junit.LiteralExpression;
import net.serenitybdd.cucumber.util.Splitter;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;

import static java.util.stream.Collectors.toList;

import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;


public class CucumberWithSerenityRuntime {

    public static Runtime using(Supplier<ClassLoader> classLoaderSupplier,
                                RuntimeOptions runtimeOptions) {
        Configuration systemConfiguration = Injectors.getInjector().getInstance(Configuration.class);
        return createSerenityEnabledRuntime(classLoaderSupplier, runtimeOptions, systemConfiguration);
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

	public static ThreadLocal<RuntimeOptions> RUNTIME_OPTIONS = new ThreadLocal<>();
	public static RuntimeOptions DEFAULT_RUNTIME_OPTIONS = buildRuntimeOptions(null);


	public static RuntimeOptions buildRuntimeOptions(Function<RuntimeOptions, RuntimeOptions> configurer) {
	    RuntimeOptions propertiesFileOptions = new CucumberPropertiesParser()
	            .parse(CucumberProperties.fromPropertiesFile())
	            .build();
	
	    RuntimeOptions annotationOptions = configurer != null ?
	    		configurer.apply(propertiesFileOptions) : propertiesFileOptions;
	
	    RuntimeOptions environmentOptions = new CucumberPropertiesParser()
	            .parse(CucumberProperties.fromEnvironment())
	            .build(annotationOptions);
	
	    RuntimeOptions runtimeOptions = new CucumberPropertiesParser()
	            .parse(CucumberProperties.fromSystemProperties())
	            .addDefaultSummaryPrinterIfNotDisabled()
	            .build(environmentOptions);
	
	    RuntimeOptionsBuilder runtimeOptionsBuilder = new RuntimeOptionsBuilder();
	    Collection<String> tagFilters = environmentSpecifiedTags(runtimeOptions.getTagExpressions());
	    for (String tagFilter : tagFilters) {
	        runtimeOptionsBuilder.addTagFilter(new LiteralExpression(tagFilter));
	    }
	    runtimeOptionsBuilder.build(runtimeOptions);
	    return runtimeOptions;
	}


	public static void setRuntimeOptions(RuntimeOptions runtimeOptions) {
	    RUNTIME_OPTIONS.set(runtimeOptions);
	}


	public static RuntimeOptions currentRuntimeOptions() {
	    return (RUNTIME_OPTIONS.get() != null) ? RUNTIME_OPTIONS.get() : DEFAULT_RUNTIME_OPTIONS;
	}


	private static Collection<String> environmentSpecifiedTags(List<?> existingTags) {
	    EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
	    String tagsExpression = ThucydidesSystemProperty.TAGS.from(environmentVariables, "");
	    List<String> existingTagsValues = existingTags.stream().map(Object::toString).collect(toList());
	    return Splitter.on(",").trimResults().omitEmptyStrings().splitToList(tagsExpression).stream()
	            .map(CucumberWithSerenityRuntime::toCucumberTag).filter(t -> !existingTagsValues.contains(t)).collect(toList());
	}


	public static String toCucumberTag(String from) {
	    String tag = from.replaceAll(":", "=");
	    if (tag.startsWith("~@") || tag.startsWith("@")) {
	        return tag;
	    }
	    if (tag.startsWith("~")) {
	        return "~@" + tag.substring(1);
	    }
	
	    return "@" + tag;
	}
}
