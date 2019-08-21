package com.serenity.screenplay.platform.android;

import com.serenity.screenplay.ProviderDriver;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import net.serenitybdd.screenplay.Actor;

/**
 * A base class representing the interactions of AndroidDriver.<br>
 * 
 * e.g: getDriver(actor).findElementsByAndroidUIAutomator(using)
 * 
 * @author jacob
 */
public class AndroidObject extends ProviderDriver<AndroidDriver<AndroidElement>> {

    @SuppressWarnings("rawtypes")
    public TouchAction withAction(Actor actor) {
        return new TouchAction(getDriver(actor));
    }


}
