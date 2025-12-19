package net.serenitybdd.cucumber;

import io.cucumber.junit.CucumberSerenityRunner;
import org.junit.runners.model.InitializationError;

/**
 * Run Cucumber with Serenity using JUnit 4.
 *
 * @deprecated JUnit 4 Cucumber support is deprecated and will be removed in Serenity 6.0.0.
 *             Please migrate to JUnit 5 (released in 2017). See the Serenity documentation
 *             for migration guidance.
 *
 *             Example migration:
 *             <pre>
 *             // OLD (JUnit 4)
 *             {@literal @}RunWith(CucumberWithSerenity.class)
 *             {@literal @}CucumberOptions(...)
 *             public class MyTests {}
 *
 *             // NEW (JUnit 5)
 *             {@literal @}Suite
 *             {@literal @}IncludeEngines("cucumber")
 *             {@literal @}SelectClasspathResource("features")
 *             {@literal @}ConfigurationParameter(
 *                 key = PLUGIN_PROPERTY_NAME,
 *                 value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel"
 *             )
 *             public class MyTests {}
 *             </pre>
 */
@Deprecated(since="5.0.0", forRemoval = true)
public class CucumberWithSerenity extends CucumberSerenityRunner {

    public CucumberWithSerenity(Class clazz) throws InitializationError {
        super(clazz);
    }
}
