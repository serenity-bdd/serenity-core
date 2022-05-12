package io.cucumber.junit;


import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class CucumberJUnit5Runner {

	public static void run(Class testClass) {
		SummaryGeneratingListener summaryGeneratingListener = new SummaryGeneratingListener();
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
          .selectors(selectClass(testClass))
          .build();
        Launcher launcher = LauncherFactory.create();
        TestPlan testPlan = launcher.discover(request);
        launcher.registerTestExecutionListeners(summaryGeneratingListener);
        launcher.execute(request);
        System.out.println(request);
    }
}
