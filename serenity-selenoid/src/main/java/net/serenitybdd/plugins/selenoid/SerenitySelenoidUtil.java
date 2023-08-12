package net.serenitybdd.plugins.selenoid;

import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.domain.ExternalLink;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.util.EnvironmentVariables;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerenitySelenoidUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerenitySelenoidUtil.class);

    public static final String SELENOID = "selenoid.";
    public static final String SELENOID_OPTIONS = "selenoid.options.";
    public static final String SELENOID_OPTIONS_CONFIG = "selenoid:options";
    public static final String SELENOID_VIDEO_DATE_PREFIX_FORMAT = "videoDatePrefixFormat";
    public static final String SELENOID_VIDEO_LINK_PREFIX = "videoLinkPrefix";
    public static final Map<String, Object> SELENOID_PROPERTIES_MAP = new HashMap<>();

    public static EnvironmentVariables env = SystemEnvironmentVariables.currentEnvironmentVariables();

    public static String getName(TestOutcome testOutcome) {
        return String.format("%s-%s-%s", getVideoDataPrefix(), testOutcome.getStoryTitle(), testOutcome.getTitle());

    }

    public static String getVideoName(TestOutcome testOutcome) {
        return String.format("%s%s", getName(testOutcome), ".mp4");
    }

    public static String getVideoDataPrefix() {
        String dataFormat = env.getProperty(SELENOID_OPTIONS + SELENOID_VIDEO_DATE_PREFIX_FORMAT, env.getProperty(String.format("%s\"%s\".%s", SELENOID, SELENOID_OPTIONS_CONFIG, SELENOID_VIDEO_DATE_PREFIX_FORMAT), "yyyy-MM-dd"));
        return DateTimeFormatter.ofPattern(dataFormat).format(LocalDateTime.now(ZoneId.systemDefault()));
    }

    protected static void linkVideoToSerenityReport(TestOutcome testOutcome) {
        try {
            URL url = new URL(env.getProperty("webdriver.remote.url"));
            String videoLinkPrefix = env.getProperty(SELENOID_OPTIONS + SELENOID_VIDEO_LINK_PREFIX, env.getProperty(String.format("%s\"%s\".%s", SELENOID, SELENOID_OPTIONS_CONFIG, SELENOID_VIDEO_LINK_PREFIX), String.format("%s:%s:8080/video", url.getProtocol(), url.getHost())));
            testOutcome.setLink(new ExternalLink(String.format("%s/%s", videoLinkPrefix, SELENOID_PROPERTIES_MAP.get("videoName")), "BrowserStack"));
        } catch (MalformedURLException e) {
            LOGGER.error("Set video link error. {}", e.getMessage());
        }

    }

    protected static Map<String, Object> getSelenoidOptionsMap(TestOutcome testOutcome) {
        // Set selenoid videoFileName and VideoName
        String name = SerenitySelenoidUtil.getName(testOutcome);
        SELENOID_PROPERTIES_MAP.put("name", name);
        SELENOID_PROPERTIES_MAP.put("videoName", getVideoName(testOutcome));
        Arrays.asList("enableVNC", "enableVideo").forEach(k -> SELENOID_PROPERTIES_MAP.put(k, true));
        return SELENOID_PROPERTIES_MAP;
    }


    protected static String unprefixed(String propertyName) {
        return propertyName.replace(SELENOID, "");
    }

}
