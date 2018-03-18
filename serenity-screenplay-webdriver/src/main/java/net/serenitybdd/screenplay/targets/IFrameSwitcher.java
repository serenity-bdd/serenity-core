package net.serenitybdd.screenplay.targets;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;


class IFrameSwitcher {

    private final static ThreadLocal<IFrameSwitcher> threadDriverManager = new ThreadLocal<>();

    static synchronized IFrameSwitcher getInstance(WebDriver driver) {
        IFrameSwitcher iFrameSwitcher = threadDriverManager.get();
        if (iFrameSwitcher == null) {
            iFrameSwitcher = new IFrameSwitcher(driver);
            threadDriverManager.set(iFrameSwitcher);
        }
        return iFrameSwitcher;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(IFrameSwitcher.class);

    private WebDriver driver;
    private IFrame currentIFrame;

    private IFrameSwitcher(WebDriver driver) {
        this.driver = driver;
    }

    void switchToIFrame(final Target target) {
        if (target.getIFrame() == null && this.currentIFrame != null) {
            LOGGER.debug("'" + target + "' Switching from " + this.currentIFrame + " to default content");
            driver.switchTo().defaultContent();
            this.currentIFrame = null;
        } else if (target.getIFrame() == null) {
            LOGGER.debug("'" + target + "' - already switched to default content");
        } else if (!target.getIFrame().equals(this.currentIFrame)) {
            String childName = target.getIFrame().childName();
            if (getWindowFrames().contains(childName)) {
                switchToChildFrame(target, childName);
            } else {
                LOGGER.debug("'" + target + "' switching to default content before switching to " + target.getIFrame());
                driver.switchTo().defaultContent();
                for (String frame : target.getIFrame().path) {
                    switchToChildFrame(target, frame);
                }
            }
            this.currentIFrame = target.getIFrame();
        } else {
            LOGGER.debug("'" + target + "' already switched to " + this.currentIFrame);
        }
    }

    private void switchToChildFrame(Target target, String frame) {
        logFrameState(target, "switching to", frame);
        driver.switchTo().frame(frame);
        logFrameState(target, "switched to", frame);
    }

    private void logFrameState(Target target, String state, String targetFrame) {
        List<String> frames = getWindowFrames();
        LOGGER.debug("'" + target + "' " + state + " iframe " + targetFrame + ". " + frames.size() + " frame(s) exist" + (frames.size() == 0 ? "" : ": " + frames));
    }

    private List<String> getWindowFrames() {
        List<String> frames = newArrayList();
        for (WebElement frame : driver.findElements(By.tagName("iframe"))) {
            String name = frame.getAttribute("name");
            if (name != null && !name.isEmpty()) {
                frames.add(name);
            }
        }
        return frames;
    }

}
