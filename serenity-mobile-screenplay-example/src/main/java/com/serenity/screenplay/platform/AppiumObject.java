package com.serenity.screenplay.platform;

import java.time.Duration;
import java.util.Set;

import org.openqa.selenium.Dimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serenity.screenplay.ProviderDriver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

/**
 * A base class representing the interactions of AppiumDriver.
 * 
 * @author jacob
 * 
 */
public abstract class AppiumObject extends ProviderDriver<AppiumDriver<MobileElement>>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AppiumObject.class);

    public void switchToWebView(Actor actor, String web_view) {
        Set<String> contextNames = getDriver(actor).getContextHandles();

        for (String contextName : contextNames) {
        	LOGGER.info("The list of webview: " + contextName);
            if (contextName.equals(web_view)) {
                getDriver(actor).context(web_view);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public TouchAction withAction(Actor actor) {
        return new TouchAction(getDriver(actor));

    }

    public void swipeDown(Actor actor) {
    	Dimension size = getDriver(actor).manage().window().getSize();

        int startY = (int) (size.getHeight() * 0.50);
        int endY = (int) (size.getHeight() * 0.20);
        int startX = size.getWidth() / 2;

        withAction(actor)
                .press(PointOption.point(startX, startY))
                .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(2)))
                .moveTo(PointOption.point(startX, endY))
                .release().perform();

    }

    public void swipeUp(Actor actor) {
    	Dimension size = getDriver(actor).manage().window().getSize();

        int startY = (int) (size.getHeight() * 0.50);
        int endY = (int) (size.getHeight() * 0.20);
        int startX = size.getWidth() / 2;

        withAction(actor)
                .press(PointOption.point(startX, endY))
                .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(2)))
                .moveTo(PointOption.point(startX, startY))
                .release().perform();

    }

    public void swipeLeft(Actor actor) {
    	Dimension size = getDriver(actor).manage().window().getSize();

        int startY = (int) (size.getHeight() / 2.0);
        int startX = (int) (size.getWidth() * 0.90);
        int endX = (int) (size.getWidth() * 0.05);

        withAction(actor)
                .longPress(PointOption.point(startX, startY))
                .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(2)))
                .moveTo(PointOption.point(endX, startY))
                .release().perform();

    }

    public void swipeRight(Actor actor) {
    	Dimension size = getDriver(actor).manage().window().getSize();
    	
        int startY = (int) (size.getHeight() / 2.0);
        int startX = (int) (size.getWidth() * 0.05);
        int endX = (int) (size.getWidth() * 0.90);

        withAction(actor)
                .longPress(PointOption.point(startX, startY))
                .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(2)))
                .moveTo(PointOption.point(endX, startY))
                .release().perform();
    }

    public final static Dimension getScreenSize(Actor actor) {
        return BrowseTheWeb.as(actor).getDriver().manage().window().getSize();
    }
    
    public void back(Actor actor) {
        getDriver(actor).navigate().back();
    }
    
    
}
