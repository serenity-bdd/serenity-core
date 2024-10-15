package net.thucydides.core.webdriver;

/**
 * Created by john on 28/10/2014.
 */
public enum MobilePlatform {
    IOS(true), ANDROID(true), WINDOWS(true), NONE(false);

    public final boolean isDefined;

    MobilePlatform(boolean isDefined) {
        this.isDefined = isDefined;
    }


}
