package net.serenitybdd.plugins.selenoid;

import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.model.ExternalLink;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class SerenitySelenoidUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerenitySelenoidUtil.class);

    public static final String SELENOID = "selenoid.";

    public static EnvironmentVariables env = SystemEnvironmentVariables.currentEnvironmentVariables();

    public static String getName(TestOutcome testOutcome) {
        return String.format("%s-%s-%s", getVideoDataPrefix(), testOutcome.getStoryTitle(), testOutcome.getTitle());

    }

    public static String getVideoName(String name) {
        return String.format("%s%s", name, getVideoNameSuffix());
    }

    public static String getVideoDataPrefix() {
        String dataFormat = env.getProperty(SELENOID + "videoDataPrefixFormat", "yyyy-MM-dd");
        return DateTimeFormatter.ofPattern(dataFormat).format(LocalDateTime.now(ZoneId.systemDefault()));
    }

    public static String getVideoNameSuffix() {
        return env.getProperty(SELENOID + "videoSuffix", ".mp4");
    }

    protected static void linkVideoToSerenityReport(TestOutcome testOutcome, String link) {
        try {
            URL url = new URL(env.getProperty("webdriver.remote.url"));
            String videoLinkPrefix = env.getProperty(SELENOID + "videoLinkPrefix", String.format("%s:%s:8080/video", url.getProtocol(), url.getHost()));
            testOutcome.setLink(new ExternalLink(String.format("%s/%s", videoLinkPrefix, link), "BrowserStack"));
        } catch (MalformedURLException e) {
            LOGGER.error("Set video link error. {}", e.getMessage());
        }

    }

    protected static String unprefixed(String propertyName) {
        return propertyName.replace(SELENOID, "");
    }

}
