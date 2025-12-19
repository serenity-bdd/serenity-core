package io.cucumber.junit;

import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;

import java.io.File;

import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PUBLISH_QUIET_PROPERTY_NAME;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClasspathResource;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectFile;


public class CucumberJUnit5ParallelRunner {

    static final String ENGINE_ID = "cucumber";


    public static EngineExecutionResults runFile(String fileName, String gluePath){
		return EngineTestKit.engine(ENGINE_ID)
				.configurationParameter(PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, "true")
				.configurationParameter(PLUGIN_PROPERTY_NAME, "net.serenitybdd.cucumber.core.plugin.SerenityReporter")
				.configurationParameter(GLUE_PROPERTY_NAME,gluePath)
				.selectors(selectFile(new File(fileName)))
				.execute();
	}

	 public static  EngineExecutionResults runFileFromClasspathInParallel(String classpathResource, String gluePath){
		return EngineTestKit.engine(ENGINE_ID)
				.configurationParameter("cucumber.execution.parallel.enabled","true")
				.configurationParameter(PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, "true")
				.configurationParameter(PLUGIN_PROPERTY_NAME, "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel")
				.configurationParameter(GLUE_PROPERTY_NAME,gluePath)
				.selectors(selectClasspathResource(classpathResource))
				.execute();
	}


}
