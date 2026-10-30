package cfg;

import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SerenityJUnit5Extension.class)
class ConfigurationTest {

    @Steps
    ConfigReader reader;

    @Test
    void readsValuesFromSerenityConf() {
        // Anywhere in the code, including a static context:
        EnvironmentVariables env = SystemEnvironmentVariables.currentEnvironmentVariables();
        EnvironmentSpecificConfiguration environmentConfig = EnvironmentSpecificConfiguration.from(env);

        // Plain keys: through the property enum, or by name
        System.out.println("CFG base.url=" + ThucydidesSystemProperty.WEBDRIVER_BASE_URL.from(env));
        System.out.println("CFG project.name=" + ThucydidesSystemProperty.SERENITY_PROJECT_NAME.from(env));
        System.out.println("CFG by-name=" + env.getProperty("webdriver.base.url"));
        System.out.println("CFG serenity-facade=" + Serenity.environmentVariables().getProperty("webdriver.base.url"));

        System.out.println("CFG my.setting=" + env.getProperty("my.setting"));

        // Keys under environments { }: ONLY through EnvironmentSpecificConfiguration
        System.out.println("CFG plain app.url=" + env.getProperty("app.url"));
        System.out.println("CFG specific app.url=" + environmentConfig.getOptionalProperty("app.url").orElse("<none>"));
        System.out.println("CFG specific db.name=" + environmentConfig.getOptionalProperty("db.name").orElse("<none>"));
        System.out.println("CFG specific company=" + environmentConfig.getOptionalProperty("company").orElse("<none>"));
        System.out.println("CFG injected=" + reader.injected() + " " + reader.appUrl());

        assertThat(ThucydidesSystemProperty.SERENITY_PROJECT_NAME.from(env)).isNotBlank();
        assertThat(reader.injected()).isTrue();
    }
}
