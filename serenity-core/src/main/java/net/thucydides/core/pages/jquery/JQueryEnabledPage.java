package net.thucydides.core.pages.jquery;

import com.google.common.io.Resources;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.core.webdriver.javascript.JavascriptExecutorFacade;
import org.openqa.selenium.WebDriver;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import static net.thucydides.core.webdriver.javascript.JavascriptSupport.javascriptIsSupportedIn;

public class JQueryEnabledPage {

    private final WebDriver driver;
    private final EnvironmentVariables environmentVariables;

    protected JQueryEnabledPage(WebDriver driver) {
        this(driver, SystemEnvironmentVariables.currentEnvironmentVariables() );
    }

    protected JQueryEnabledPage(WebDriver driver, EnvironmentVariables environmentVariables) {
        this.driver = driver;
        this.environmentVariables = environmentVariables;

    }

    public static JQueryEnabledPage withDriver(final WebDriver driver) {
        return new JQueryEnabledPage(driver);
    }

	public boolean isJQueryIntegrationEnabled(){
		boolean jqueryIntegrationEnabled =
				Boolean.valueOf(ThucydidesSystemProperty.SERENITY_JQUERY_INTEGRATION
						.from(environmentVariables,"true"));
		return jqueryIntegrationEnabled;
	}

    public boolean isJQueryAvailable() {
        boolean jqueryIntegrationEnabled = isJQueryIntegrationEnabled();
        if (jqueryIntegrationEnabled && javascriptIsSupportedIn(driver)) {
            try {
                JavascriptExecutorFacade js = new JavascriptExecutorFacade(driver);
                Boolean result = (Boolean) js.executeScript("return (typeof jQuery === 'function')");
                return ((result != null) && (result));
            } catch (RuntimeException jsExecutionFailed) {
                return false;
            }
        }
        return false;
    }


    public void injectJQuery() {
        executeScriptFrom("jquery/jquery.min.js");
        executeScriptFrom("jquery/jquery.focus.test-fix.js");
    }

    protected void executeScriptFrom(String scriptSource) {
        if (javascriptIsSupportedIn(driver)) {
            String script = getFileAsString(scriptSource);
            JavascriptExecutorFacade js = new JavascriptExecutorFacade(driver);
            js.executeScript(script);
        }
    }

    private String getFileAsString(final String resourcePath) {
        String content = "";
        try {
            URL fileUrl = getClass().getClassLoader().getResource(resourcePath);
            content = Resources.toString(fileUrl, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return content;
    }

    public void injectJQueryPlugins() {
        if (ThucydidesSystemProperty.SERENITY_ACTIVATE_HIGHLIGHTING.booleanFrom(environmentVariables)) {
            executeScriptFrom("jquery/jquery-thucydides-plugin.js");
        }
    }

    public void injectJavaScriptUtils(){
    	executeScriptFrom("javascript/cycle.js");
    }

    public void activateJQuery() {

        if (isJQueryIntegrationEnabled() && !isJQueryAvailable()) {
            injectJQuery();
            injectJQueryPlugins();
        }
    }
}
