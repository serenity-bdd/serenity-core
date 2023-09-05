package net.thucydides.core.webdriver.healenium;

import com.epam.healenium.SelfHealingDriver;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealeniumWrapper {
    private static final Logger logger = LoggerFactory.getLogger(HealeniumWrapper.class);

    public static WebDriver wrapDriver(WebDriver driver) {
        if (isResourcePresent("healenium.properties")) {
            logger.info("Healenium driver activated");
            return SelfHealingDriver.create(driver);
        } else {
            logger.info("No helenium.properties file found - normal driver used");
            return driver;
        }
    }

    public static boolean isResourcePresent(String resourceName) {
        // Use the class loader to fetch the resource
        return HealeniumWrapper.class.getClassLoader().getResource(resourceName) != null;
    }
}
