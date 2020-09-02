package net.thucydides.core.steps;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Builds a file path by substituting environment variables.
 * Supported environment variables include $HOME, $USERDIR and $DATADIR.
 * $DATADIR is provided by setting the serenity.data.dir environment property.
 */
public class FilePathParser {
    private final EnvironmentVariables environmentVariables;

    public FilePathParser(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public String getInstanciatedPath(String path) {
        if (path == null) {
            return path;
        }
        String localizedPath = operatingSystemLocalized(path);
        localizedPath = injectVariable(localizedPath, "HOME", valueDefinedIn(environmentVariables,"user.home"));
        localizedPath = injectVariable(localizedPath, "user.home", valueDefinedIn(environmentVariables,"user.home"));
        localizedPath = injectVariable(localizedPath, "USERDIR", valueDefinedIn(environmentVariables,"user.dir"));
        localizedPath = injectVariable(localizedPath, "USERPROFILE", valueDefinedIn(environmentVariables,"user.home"));
        localizedPath = injectVariable(localizedPath, "user.dir", valueDefinedIn(environmentVariables,"user.dir"));
        localizedPath = injectVariable(localizedPath, "APPDATA", valueDefinedIn(environmentVariables,"APPDATA"));
        localizedPath = injectVariable(localizedPath, "DATADIR",valueDefinedIn(environmentVariables,"serenity.data.dir"));

        return localizedPath;
    }

    private String valueDefinedIn(EnvironmentVariables environmentVariables, String propertyName) {
        return EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(propertyName)
                .orElse(environmentVariables.getValue(propertyName));
//
//        return (environmentVariables.getValue(propertyName) != null)
//                ? environmentVariables.getValue(propertyName) : environmentVariables.getProperty(propertyName);
    }

    private String operatingSystemLocalized(String testDataSource) {
        return StringUtils.replace(testDataSource, getFileSeparatorToReplace(), getFileSeparator());
    }

    private String injectVariable(String path, String variable, String directory) {
        if (StringUtils.isNotEmpty(directory)) {
            path = StringUtils.replace(path, "$" + variable, directory);
            path = StringUtils.replace(path, "%" + variable + "%", directory);
            return StringUtils.replace(path, "${" + variable + "}", directory);
        } else {
            return path;
        }
    }

    protected String getFileSeparator() {
        return File.separator;
    }

    protected String getFileSeparatorToReplace() {
        return (getFileSeparator().equals("/")) ? "\\" : "/";
    }
}
