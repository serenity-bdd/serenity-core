package com.serenity.screenplay.platform.ios;

import com.serenity.screenplay.ProviderDriver;

import io.appium.java_client.TouchAction;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import net.serenitybdd.screenplay.Actor;

/**
 * A base class representing the interactions of IOSDriver.
 * <br>
 * e.g: getDriver(actor).findElementsByIosUIAutomation(using);
 * 
 * @author jacob
 */
public abstract class IOSObject extends ProviderDriver<IOSDriver<IOSElement>> {

    @SuppressWarnings("rawtypes")
    public TouchAction withAction(Actor actor) {
        return new TouchAction(getDriver(actor));
    }
 
}
