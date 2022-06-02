package io.cucumber.junit;

import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;

import java.io.File;

import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PUBLISH_QUIET_PROPERTY_NAME;
import static org.junit.platform.engine.discovery.DiscoverySelectors.*;


public class CucumberJUnit5Runner {


    static final String ENGINE_ID = "cucumber";

	/*public static void run(Class testClass) {
		SummaryGeneratingListener summaryGeneratingListener = new SummaryGeneratingListener();
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
          .selectors(selectClass(testClass))
          .build();
        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(summaryGeneratingListener);
        launcher.execute(request);
    }*/

    public static EngineExecutionResults runFile(String fileName, String gluePath){
		return EngineTestKit.engine(ENGINE_ID)
				.configurationParameter(PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, "true")
				.configurationParameter(PLUGIN_PROPERTY_NAME, "io.cucumber.core.plugin.SerenityReporter")
				.configurationParameter(GLUE_PROPERTY_NAME,gluePath)
				.selectors(selectFile(new File(fileName)))
				.execute();
	}

	 public static  EngineExecutionResults runFileFromClasspath(String classpathResource, String gluePath){
		return EngineTestKit.engine(ENGINE_ID)
				.configurationParameter(PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, "true")
				.configurationParameter(PLUGIN_PROPERTY_NAME, "io.cucumber.core.plugin.SerenityReporter")
				.configurationParameter(GLUE_PROPERTY_NAME,gluePath)
				.selectors(selectClasspathResource(classpathResource))
				.execute();
	}


}
