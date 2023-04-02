package net.thucydides.core.webdriver.capabilities;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import io.cucumber.java.hu.Ha;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static net.thucydides.core.webdriver.CapabilityValue.stripQuotesFrom;

public class W3CCapabilities {

    private final EnvironmentVariables environmentVariables;
    private static final Logger LOGGER = LoggerFactory.getLogger(W3CCapabilities.class);
    private String prefix;
    private final Set<String> STRING_CONFIG_PROPERTIES = new HashSet<>(Arrays.asList("platformName","platformVersion"));

    public W3CCapabilities(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static W3CCapabilities definedIn(EnvironmentVariables environmentVariables) {
        return new W3CCapabilities(environmentVariables);
    }

    private final List<String> BASE_PROPERTIES = Arrays.asList("browserName", "browserVersion", "platformName");

    public W3CCapabilities withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public DesiredCapabilities asDesiredCapabilities() {
        return getDesiredCapabilities();
    }

    private DesiredCapabilities getDesiredCapabilities() {
        Config browserConfig = EnvironmentSpecificConfiguration.from(environmentVariables).getConfig(this.prefix);
        return convertToCapabilities(browserConfig);
    }

    protected DesiredCapabilities convertToCapabilities(Config config) {
        DesiredCapabilities capabilities = capacitiesWithStandardBrowerProperties(config);
        addNonMandatoryCapabilities(config, capabilities);
        return capabilities;
    }

    private void addNonMandatoryCapabilities(Config config, DesiredCapabilities capabilities) {
        List<String> fieldNames = nonMandatoryFieldNamesIn(config);

        for (String fieldName : leafFieldNamesIn(fieldNames)) {
            addCapabilityValue(config, capabilities, fieldName);
        }
        for (String fieldGroup : parentFieldNamesIn(fieldNames)) {
            addNestedCapabilityValues(config, capabilities, fieldGroup);
        }
    }

    @NotNull
    private List<String> nonMandatoryFieldNamesIn(Config config) {
        return config.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    @NotNull
    private DesiredCapabilities capacitiesWithStandardBrowerProperties(Config config) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        if (hasValueFor("browserName", ConfigValueType.STRING, config)) {
            capabilities.setBrowserName(config.getString("browserName"));
        }
        if (hasValueFor("browserVersion", ConfigValueType.STRING, config)) {
            capabilities.setVersion(config.getString("browserVersion"));
        }
        if (hasValueFor("platformVersion", ConfigValueType.STRING, config)) {
            capabilities.setVersion(config.getString("platformVersion"));
        }
        if (hasValueFor("platformName", ConfigValueType.STRING, config)) {
            String platformName = config.getString("platformName");
            capabilities.setCapability("platformName", platformName);
            try {
                capabilities.setPlatform(Platform.fromString(platformName));
            } catch (WebDriverException unknownPlatformValueSoLetsSetItAsAStringAndHopeForTheBest) {
                LOGGER.warn("Unknown platform name (ignoring): " + platformName);
            }
        }
        if (hasValueFor("acceptInsecureCerts", ConfigValueType.BOOLEAN, config)) {
            capabilities.setAcceptInsecureCerts(config.getBoolean("acceptInsecureCerts"));
        }
        return capabilities;
    }

    private boolean hasValueFor(String propertyName, ConfigValueType valueType, Config config) {
        return config.hasPath(propertyName) && config.getValue(propertyName).valueType() == valueType;
    }

    private void addNestedCapabilityValues(Config config, DesiredCapabilities capabilities, String fieldGroup) {
        Config groupConfig = config.getConfig(fieldGroup);
        groupConfig.entrySet();
        if (fieldGroup.equals("proxy")) {
            capabilities.setCapability(fieldGroup, new Proxy(asMap(groupConfig)));
        } else if (isCustom(fieldGroup)) {
            DesiredCapabilities nestedCapabilites = new DesiredCapabilities();
            addNonMandatoryCapabilities(groupConfig, nestedCapabilites);
            capabilities.setCapability(stripQuotesFrom(fieldGroup), nestedCapabilites.asMap());
        } else {
            capabilities.setCapability(stripQuotesFrom(fieldGroup), asMap(groupConfig));
        }
    }

    private boolean isCustom(String fieldGroup) {
        return fieldGroup.contains(":");
    }

    private Map<String, Object> asMap(Config groupConfig) {
        Map<String, Object> values = new HashMap<>();
        groupConfig.entrySet().forEach(
                entry -> values.put(stripQuotesFrom(entry.getKey()), asObject(entry.getValue()))
        );
        return values;
    }

    private void addCapabilityValue(Config config, DesiredCapabilities capabilities, String fieldName) {
        ConfigValue value = config.getValue(fieldName);
        if (STRING_CONFIG_PROPERTIES.contains(fieldName)) {
            capabilities.setPlatform(Platform.fromString(value.unwrapped().toString()));
        } else {
            capabilities.setCapability(fieldName, asObject(value));
        }
    }

    private Object asObject(ConfigValue value) {
        switch (value.valueType()) {
            case LIST:
                return ((ConfigList) value).stream()
                        .map(ConfigValue::unwrapped)
                        .collect(Collectors.toList());
            case STRING:
            case BOOLEAN:
            case NUMBER:
            default:
                return value.unwrapped();
        }
    }

    @NotNull
    private List<String> parentFieldNamesIn(List<String> fieldNames) {
        return fieldNames.stream()
                .filter(fieldName -> fieldName.contains("."))
                .map(fieldName -> fieldName.substring(0, fieldName.indexOf(".")))
                .distinct()
                .collect(Collectors.toList());
    }

    @NotNull
    private List<String> leafFieldNamesIn(List<String> fieldNames) {
        return fieldNames.stream()
                .filter(entry -> !BASE_PROPERTIES.contains(entry))
                .filter(fieldName -> !fieldName.contains("."))
                .collect(Collectors.toList());
    }

    public ChromeOptions chromeOptions() {
        ChromeOptions options = (ChromeOptions) ChromiumOptionsBuilder.fromDesiredCapabilities(asDesiredCapabilities(),
                new ChromeOptions(),
                ChromeOptions.CAPABILITY);

        options.addArguments("remote-allow-origins=*");
        return options;
    }

    public EdgeOptions edgeOptions() {
        return (EdgeOptions) ChromiumOptionsBuilder.fromDesiredCapabilities(asDesiredCapabilities(),
                new EdgeOptions(),
                EdgeOptions.CAPABILITY);
    }

    public FirefoxOptions firefoxOptions() {
        return FirefoxOptionsBuilder.fromDesiredCapabilities(asDesiredCapabilities());
    }

    public SafariOptions safariOptions() {
        return SafariOptions.fromCapabilities(asDesiredCapabilities());
    }

    public InternetExplorerOptions internetExplorerOptions() {
        return new InternetExplorerOptions(asDesiredCapabilities());
    }

    public <D extends MutableCapabilities> D forDriver(SupportedWebDriver driver) {
        switch (driver) {
            case CHROME:
                return (D) chromeOptions();
            case EDGE:
                return (D) edgeOptions();
            case FIREFOX:
                return (D) firefoxOptions();
            default:
                return (D) asDesiredCapabilities();
        }
    }
}
