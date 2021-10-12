package net.serenitybdd.core.pages;

import net.serenitybdd.core.exceptions.SerenityManagedException;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.InvalidPathException;

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
