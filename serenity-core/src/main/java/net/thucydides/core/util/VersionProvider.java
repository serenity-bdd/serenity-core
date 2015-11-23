package net.thucydides.core.util;

import net.thucydides.core.guice.Injectors;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by john on 19/06/2014.
 */
public class VersionProvider {


    private final EnvironmentVariables environmentVariables;

    public VersionProvider() {
        this(Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    public VersionProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public String getVersion()
    {
        String path = "/serenity-version.properties";
        Properties props = new Properties();
        try (InputStream stream = getClass().getResourceAsStream(path)){
            if (stream == null)
                return "UNKNOWN";
            props.load(stream);
            stream.close();
            return (String) props.get("application.version");
        } catch (IOException e) {
            return "UNKNOWN";
        }
    }

    public String getBuildNumberText() {
        return environmentVariables.getValue(buildNumberVariable(),"UNKNOWN");
    }

    private String buildNumberVariable() {
        return environmentVariables.getProperty("build.number.variable","BUILD_NUMBER");
    }
}
