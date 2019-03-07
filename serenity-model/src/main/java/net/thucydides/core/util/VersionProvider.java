package net.thucydides.core.util;

import net.thucydides.core.guice.Injectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Properties;

/**
 * Created by john on 19/06/2014.
 */
public class VersionProvider {


    private final EnvironmentVariables environmentVariables;

    private final Logger logger = LoggerFactory.getLogger(VersionProvider.class);

    public VersionProvider() {
        this(Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    public VersionProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public String getVersion()
    {
        String buildNumber = "UNKNOWN";
        try {
            ProtectionDomain protectionDomain = this.getClass().getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            buildNumber = getVersionNameFromArchiveName(codeSource.getLocation());
        } catch(Throwable t){
            logger.error("Cannot get version number ", t);
            return getVersionLegacyWay();
        }
        return buildNumber;
    }


    public String getVersionNameFromArchiveName(URL locationUrl) {
        String locationString = locationUrl.toString();
        String buildNumber;
        String archiveName = locationString.substring(locationString.lastIndexOf(File.separator) + 1,locationString.length()-4);
        if(archiveName.endsWith("-SNAPSHOT")) {
            String archiveNameNoSnapshot = archiveName.substring(0,archiveName.length()-9);
            buildNumber = archiveName.substring(archiveNameNoSnapshot.lastIndexOf("-")+1);
        } else {
            buildNumber = archiveName.substring(archiveName.lastIndexOf("-")+1);
        }
        return buildNumber;
    }


    public String getVersionLegacyWay()
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
