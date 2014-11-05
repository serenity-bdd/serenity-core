package net.thucydides.core.digest;

import org.apache.commons.codec.digest.DigestUtils;

public class Digest {
    public static String ofTextValue(final String text) {
        return DigestUtils.sha256Hex(text);
    }
}
