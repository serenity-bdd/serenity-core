package net.thucydides.core.steps;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import java.io.File;

/**
 * Builds a file path by substituting environment variables.
 * Supported environment variables include $HOME, $USERDIR and $DATADIR.
 * $DATADIR is provided by setting the thucydides.data.dir environment property.
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
        localizedPath = injectVariable(localizedPath, "HOME", environmentVariables.getProperty("user.home"));
        localizedPath = injectVariable(localizedPath, "user.home", environmentVariables.getProperty("user.home"));
        localizedPath = injectVariable(localizedPath, "USERDIR", environmentVariables.getProperty("user.dir"));
        localizedPath = injectVariable(localizedPath, "USERPROFILE", environmentVariables.getProperty("user.home"));
        localizedPath = injectVariable(localizedPath, "user.dir", environmentVariables.getProperty("user.dir"));
        localizedPath = injectVariable(localizedPath, "APPDATA", environmentVariables.getValue("APPDATA"));
        localizedPath = injectVariable(localizedPath, "DATADIR",
                                        ThucydidesSystemProperty.THUCYDIDES_DATA_DIR.from(environmentVariables));

        return localizedPath;
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
