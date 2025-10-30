package net.thucydides.core.steps;

import net.serenitybdd.model.di.ModelInfrastructure;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Capabilities;

import java.util.ArrayList;
import java.util.List;

import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_ADD_OS_TAG;

public class TestContext {

    private static ThreadLocal<TestContext> LOCAL_TEST_CONTEXT = ThreadLocal.withInitial(TestContext::new);

    private String browserName;
    private String platform;

    public static TestContext forTheCurrentTest() {
        return LOCAL_TEST_CONTEXT.get();
    }

    public void recordBrowser(String browserName) {
        if (browserName != null) {
            this.browserName = browserName;
        }
    }

    public void recordPlatform(String platform) {
        if (platform != null) {
            this.platform = platform;
        }
    }

    public String getContext() {
        List<String> contextItems = new ArrayList<>();
        if (StringUtils.isNotBlank(browserName)) {
            contextItems.add(browserName.toLowerCase());
        }

        if (StringUtils.isNotBlank(platform)) {
            contextItems.add(platform.toLowerCase());
        } else if (SERENITY_ADD_OS_TAG.booleanFrom(ModelInfrastructure.getEnvironmentVariables())) {
            contextItems.add(currentPlatform());
        }

        return String.join(",", contextItems);
    }

    private String currentPlatform() {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            return "windows";
        } else if (osName.startsWith("Mac")) {
            return "mac";
        } else if (osName.startsWith("Linux")) {
            return "linux";
        } else {
            return osName;
        }
    }

    public void recordCurrentPlatform() {
        recordPlatform(currentPlatform());
    }

    public void recordBrowserConfiguration(Capabilities options) {
        recordBrowser(options.getBrowserName());
    }

    public void recordBrowserAndPlatformConfiguration(Capabilities options) {
        recordBrowser(options.getBrowserName());
        if (options.getPlatformName() != null) {
            recordPlatform(options.getPlatformName().name());
        }
    }

}
