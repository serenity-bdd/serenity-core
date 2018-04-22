package net.serenitybdd.screenplay.targets;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.util.Optional.empty;

class IFrameSwitcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(IFrameSwitcher.class);
    private final static ThreadLocal<IFrameSwitcher> frameSwitcher = new ThreadLocal<>();
    private Optional<IFrame> currentIFrame = empty();

    static synchronized IFrameSwitcher getInstance() {
        IFrameSwitcher iFrameSwitcher = frameSwitcher.get();
        if (iFrameSwitcher == null) {
            iFrameSwitcher = new IFrameSwitcher();
            frameSwitcher.set(iFrameSwitcher);
        }
        return iFrameSwitcher;
    }


    private IFrameSwitcher() {
    }

    void switchToIFrame(WebDriver driver, final Target target) {
        if (target.getIFrame().equals(this.currentIFrame)) {
            LOGGER.debug("{} already selected for {}", printIFrame(this.currentIFrame), target);
        } else if (!target.getIFrame().isPresent() && this.currentIFrame.isPresent()) {
            LOGGER.debug("switching from {} to {} for {}", printIFrame(this.currentIFrame), printIFrame(target.getIFrame()), target);
            driver.switchTo().defaultContent();
            this.currentIFrame = empty();
        } else {
            driver.switchTo().defaultContent();
            target.getIFrame().ifPresent(iFrame -> iFrame.locators.forEach(frameLocator -> driver.switchTo().frame(driver.findElement(frameLocator))));
            this.currentIFrame = target.getIFrame();
        }
    }

    private String printIFrame(Optional<IFrame> frame) {
        return frame.map(IFrame::toString).orElse("default content");
    }

}
