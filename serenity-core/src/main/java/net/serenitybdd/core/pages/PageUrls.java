package net.serenitybdd.core.pages;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.NamedUrl;
import net.thucydides.core.annotations.NamedUrls;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * Manage the URLs associated with a page
 * Urls can be associated with a page using annotations or via the default configuration properties.
 * The DefaultUrl annotation defines the default starting point for a page. If none is defined, the
 * system default URL is used.
 * The NamedUrl and NamedUrls annotations can be used to define query URLs, optionally with parameters.
 */
public class PageUrls {
    private static final String CLASSPATH_URL_PREFIX = "classpath:";
    private static final String NAMED_URL_PREFIX = "page:";
    private static final int CLASSPATH_URL_PREFIX_LENGTH = CLASSPATH_URL_PREFIX.length();
    private Object pageObject;

    private String pageLevelDefaultBaseUrl;

    private final Configuration configuration;

    public PageUrls(final Object pageObject, final Configuration configuration) {
        this.pageObject = pageObject;
        this.configuration = configuration;
    }

    public PageUrls(final Object pageObject) {
        this(pageObject, ConfiguredEnvironment.getConfiguration());
    }

    public PageUrls(final Object pageObject, EnvironmentVariables environmentVariables) {
        this(pageObject,
             ConfiguredEnvironment.getConfiguration().withEnvironmentVariables(environmentVariables));
    }

    public String getStartingUrl() {
        Optional<String> declaredDefaultUrl = getDeclaredDefaultUrl();
        String url;
        if (declaredDefaultUrl.isPresent()) {
            url = addBaseUrlTo(declaredDefaultUrl.get());
        } else {
            url = getBaseUrl();
        }
        return verified(url, pageObject);
    }

    public Optional<String> getDeclaredDefaultUrl() {
        DefaultUrl urlAnnotation = pageObject.getClass().getAnnotation(DefaultUrl.class);
        if (urlAnnotation != null) {
            return Optional.ofNullable(urlAnnotation.value());
        } else {
            return Optional.empty();
        }
    }

    public String verified(String requestedUrl, Object pageObject) {
        if (isAClasspathResource(requestedUrl)) {
            return obtainResourcePathFromClasspath(requestedUrl).toString();
        } else {
            try {
                URL url = new URL(requestedUrl);
                return url.toString();
            } catch (MalformedURLException e) {
                if (requestedUrl == null) {
                    throw new AssertionError("Undefined default URL for page object "
                            + pageObject.getClass().getSuperclass().getSimpleName());
                } else {
                    throw new AssertionError("Invalid URL: " + requestedUrl);
                }
            }
        }
    }

    public static String getUrlFrom(final String annotatedBaseUrl) {
        if (annotatedBaseUrl == null) {
            return null;
        }
        if (isAClasspathResource(annotatedBaseUrl)) {
            URL baseUrl = obtainResourcePathFromClasspath(annotatedBaseUrl);
            return baseUrl.toString();
        } else if (isANamedUrl(annotatedBaseUrl)) {
            return namedUrlFrom(annotatedBaseUrl);
        } else {
            return annotatedBaseUrl;
        }
    }
    private static boolean isANamedUrl(String annotatedBaseUrl) {
        return annotatedBaseUrl.startsWith(NAMED_URL_PREFIX);
    }

    private static boolean isAClasspathResource(String annotatedBaseUrl) {
        return (annotatedBaseUrl != null) && (annotatedBaseUrl.startsWith(CLASSPATH_URL_PREFIX));
    }

    private static URL obtainResourcePathFromClasspath(final String classpathUrl) {
        String resourcePath = classpathUrl.substring(CLASSPATH_URL_PREFIX_LENGTH);
        URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            throw new IllegalStateException("No matching web page could be found on the classpath for " + classpathUrl);
        }
        return resourceUrl;
    }


    private static String namedUrlFrom(String annotatedBaseUrl) {
        String pageName = annotatedBaseUrl.substring(NAMED_URL_PREFIX.length());
        EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty(pageName)
                .orElse(environmentVariables.getProperty(pageName));
    }


    private String getBaseUrl() {
        String baseUrl;
        if (StringUtils.isNotEmpty(getSystemBaseUrl())) {
            baseUrl = getSystemBaseUrl();
        } else {
            baseUrl = pageLevelDefaultBaseUrl;
        }

        return getUrlFrom(baseUrl);
    }

    public String getStartingUrl(final String... parameterValues) {
        String startingUrlTemplate = getStartingUrl();
        return urlWithParametersSubstituted(startingUrlTemplate, parameterValues);
    }

    public String getNamedUrl(final String name) {
        NamedUrls urlAnnotation = pageObject.getClass().getAnnotation(NamedUrls.class);
        if (urlAnnotation != null) {
            NamedUrl[] namedUrlList = urlAnnotation.value();
            for (NamedUrl namedUrl : namedUrlList) {
                if (namedUrl.name().equals(name)) {
                    return prefixedWithDefaultUrl(namedUrl.url());
                }
            }
        }
        throw new IllegalArgumentException("No URL named " + name
                + " was found in this class");
    }

    private String prefixedWithDefaultUrl(String url) {
        Optional<String> optionalDeclaredDefaultUrl = getDeclaredDefaultUrl();
        if (optionalDeclaredDefaultUrl.isPresent() && isARelativeUrl(url)) {
            String declaredDefaultUrl = optionalDeclaredDefaultUrl.get();
            if (isANamedUrl(declaredDefaultUrl)) {
                declaredDefaultUrl = namedUrlFrom(declaredDefaultUrl);
            }
            return StringUtils.stripEnd(declaredDefaultUrl, "/")
                    + "/"
                    + StringUtils.stripStart(url, "/");
        } else {
            return url;
        }
    }

    public String getNamedUrl(final String name,
                              final String[] parameterValues) {
        String startingUrlTemplate = getNamedUrl(name);
        return urlWithParametersSubstituted(startingUrlTemplate, parameterValues);
    }

    private String urlWithParametersSubstituted(final String template,
                                                final String[] parameterValues) {

        String url = addBaseUrlTo(template);
        for (int i = 0; i < parameterValues.length; i++) {
            String variable = String.format("{%d}", i + 1);
            url = url.replace(variable, parameterValues[i]);
        }
//        return addBaseUrlTo(url);
        return url;
    }



    public String addDefaultUrlTo(final String url) {
        return prefixedWithDefaultUrl(url);
    }

    public String addBaseUrlTo(final String url) {
        if (isANamedUrl(url)) {
            return namedUrlFrom(url);
        }

        if (isAClasspathResource(url) && baseUrlIsDefined()) {
            return getBaseUrl();
        }
        if (isARelativeUrl(url)) {
            return updatedRelativeUrl(url);
        }
        return updatedFullUrl(url);
    }

    private boolean baseUrlIsDefined() {
        return StringUtils.isNotEmpty(getBaseUrl());
    }

    private String updatedFullUrl(String url) {
        if (isAClasspathResource(url)) {
            return url;
        } else {
            String updatedUrl = url;
            if (StringUtils.isNotEmpty(getBaseUrl())) {
                try {
                    updatedUrl = removeDoubleSlashesFrom(getBaseUrl() + pathFrom(url));
                } catch (MalformedURLException e) {
                    throw new AssertionError("Invalid URL: " + url);
                }
            }
            return updatedUrl;
        }
    }

    private String removeDoubleSlashesFrom(String url) {
        return url.replaceAll("([^:])//", "$1/");
    }

    private String pathFrom(String url) throws MalformedURLException {
        URL defaultUrl = new URL(url);
        return url.substring(url.indexOf(defaultUrl.getAuthority()) + defaultUrl.getAuthority().length());
    }

    private String updatedRelativeUrl(String url) {
        String updatedUrl;
        if (StringUtils.isNotEmpty(getBaseUrl())) {
            updatedUrl = getBaseUrl() + url;
        } else {
            updatedUrl = url;
        }
        return updatedUrl;
    }

    private boolean isARelativeUrl(final String url) {
        return url.startsWith("/");
    }


    public void overrideDefaultBaseUrl(final String defaultBaseUrl) {
        pageLevelDefaultBaseUrl = defaultBaseUrl;
    }

    public String getSystemBaseUrl() {
        return configuration.getBaseUrl();
    }
}
