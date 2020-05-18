package net.serenitybdd.screenplay.targets;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;

import static java.util.Optional.empty;

class IFrameSwitcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(IFrameSwitcher.class);
    private final static ThreadLocal<IFrameSwitcher> frameSwitcher = new ThreadLocal<>();
    private final HashMap<WebDriver,Optional<IFrame>> currentIFrames = new HashMap<>();

    static synchronized IFrameSwitcher getInstance() {
        IFrameSwitcher iFrameSwitcher = frameSwitcher.get();
        if (iFrameSwitcher == null) {
            iFrameSwitcher = new IFrameSwitcher();
            frameSwitcher.set(iFrameSwitcher);
        }
        return iFrameSwitcher;
    }

    private IFrameSwitcher() {}

    void switchToIFrame(WebDriver driver, final Target target) {
        if (!currentIFrames.containsKey(driver)) {
            currentIFrames.put(driver, empty());
        }
        Optional<IFrame> currentIFrame = currentIFrames.get(driver);
        if (target.getIFrame().equals(currentIFrame)) {
            LOGGER.debug("{} already selected for {}", printIFrame(currentIFrame), target);
        } else if (!target.getIFrame().isPresent() && currentIFrame.isPresent()) {
            LOGGER.debug("switching from {} to {} for {}", printIFrame(currentIFrame), printIFrame(target.getIFrame()), target);
            driver.switchTo().defaultContent();
            currentIFrames.put(driver, empty());
        } else {
            driver.switchTo().defaultContent();
            target.getIFrame().ifPresent(iFrame -> iFrame.locators.forEach(frameLocator -> driver.switchTo().frame((WebElement)driver.findElement(frameLocator))));
            currentIFrames.put(driver, target.getIFrame());
        }
    }

    private String printIFrame(Optional<IFrame> frame) {
        return frame.map(IFrame::toString).orElse("default content");
    }

}