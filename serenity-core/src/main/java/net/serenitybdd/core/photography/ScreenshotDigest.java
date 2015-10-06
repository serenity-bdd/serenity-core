package net.serenitybdd.core.photography;

import org.apache.commons.codec.digest.DigestUtils;

public class ScreenshotDigest {

    public static String forScreenshotData(byte[] screenshotData) {
        return DigestUtils.md5Hex(screenshotData) + ".png";
    }
}