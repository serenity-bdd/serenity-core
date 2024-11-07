package net.serenitybdd.cucumber.suiteslicing;

import io.cucumber.junit.platform.engine.Constants;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestRunStarted;
import org.junit.platform.suite.api.*;


@Suite(failIfNoTests = false)
@IncludeEngines("cucumber-batch")
@SelectClasspathResources({@SelectClasspathResource("path-1"), @SelectClasspathResource("path-2")})
@ConfigurationParameters({
        @ConfigurationParameter(key = Constants.EXECUTION_DRY_RUN_PROPERTY_NAME, value = "false"),
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.example.support,com.example.steps,com.example.hooks,com.example.types"),
        @ConfigurationParameter(key = Constants.OBJECT_FACTORY_PROPERTY_NAME, value = "cucumber.runtime.SerenityObjectFactory"),
        @ConfigurationParameter(key = Constants.PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true"),
        @ConfigurationParameter(key = Constants.PLUGIN_PUBLISH_ENABLED_PROPERTY_NAME, value = "false"),
        @ConfigurationParameter(key = Constants.PARALLEL_EXECUTION_ENABLED_PROPERTY_NAME, value = "false"),
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "SlicedTestRunner,io.cucumber.core.plugin.SerenityReporter,pretty"),
        @ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "@my-tag")})
public class SlicedTestRunner implements ConcurrentEventListener {


    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestRunStarted.class, event -> {
            setUp();
        });
        eventPublisher.registerHandlerFor(TestRunFinished.class, event -> {
            tearDown();
        });
    }

    public static void setUp() {
        // setup code here
    }

    public static void tearDown() {
        // tearDown code here
    }
}
