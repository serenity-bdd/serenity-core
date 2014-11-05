package net.thucydides.core.util;

import java.io.File;
import java.net.URL;

/**
 * Created by john on 27/06/2014.
 */
public class URLUtils {

    public static File urlToFile(final URL url) {
        try {
            return new File(url.toURI());
        } catch (final Exception e) {
            throw new RuntimeException("failed to convert URL [" + url + "] to File", e);
        }
    }
}
