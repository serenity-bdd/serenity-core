package net.serenitybdd.core.photography;

import org.apache.commons.codec.digest.DigestUtils;

public class ScreenshotDigest {

    public static String forScreenshotData(byte[] screenshotData) {
        return DigestUtils.sha256Hex(screenshotData) + ".png";
    }
}