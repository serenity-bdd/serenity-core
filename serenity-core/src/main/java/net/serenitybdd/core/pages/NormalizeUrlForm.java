package net.serenitybdd.core.pages;

import java.net.URISyntaxException;
import java.net.URL;

public class NormalizeUrlForm {
    public static String ofUrl(String baseUrl) {
        if (baseUrl.startsWith("classpath:")) {
            try {
                URL pageUrl = Thread.currentThread().getContextClassLoader().getResource(baseUrl.replace("classpath:/",""));
                if (pageUrl == null) {
                    throw new IllegalArgumentException("Resource not found for on the classpath for " + baseUrl);
                }
                return pageUrl.toURI().toString();
            } catch (URISyntaxException shouldNeverHappen) {
                return baseUrl;
            }
        } else {
            return baseUrl;
        }
    }
}
