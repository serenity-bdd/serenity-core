package net.serenitybdd.junit5;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

public class TestLauncher {

    private TestLauncher(){}

    public static void runTestWithPackageNameForClass(String packageName,Class testClass)
    {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectPackage(packageName),
                        selectClass(testClass)
                )
                .build();
        LauncherFactory.create().execute(request);
    }

    public static void runTestForClass(Class testClass){
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();
        LauncherFactory.create().execute(request);
    }

    public static void runTestForClass(Launcher launcher, Class testClass){
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();
        launcher.execute(request);
    }
}
