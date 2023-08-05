package net.thucydides.model.util;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.environment.SystemEnvironmentVariables;
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
        this(SystemEnvironmentVariables.currentEnvironmentVariables() );
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
            buildNumber = getVersionNameFromCodeSourceURL(codeSource.getLocation());
        } catch(Throwable t){
            logger.error("Cannot get version number ", t);
            return getVersionLegacyWay();
        }
        return buildNumber;
    }

    String getVersionNameFromCodeSourceURL(URL locationUrl) {
        String archiveLoation = locationUrl.toString();
        String buildNumber;

        String archiveName = (archiveLoation.endsWith(".jar")) ?
                                archiveLoation.substring(archiveLoation.lastIndexOf(File.separator) + 1,archiveLoation.length() - ".jar".length()) : "localbuild";

        if(archiveName.endsWith("-SNAPSHOT")) {
            String archiveNameWithoutSnapshotEnding = archiveName.substring(0,archiveName.length()- "-SNAPSHOT".length());
            buildNumber = archiveName.substring(archiveNameWithoutSnapshotEnding.lastIndexOf("-")+1);
        } else if(archiveName.endsWith("-all")) {
            //there is no jar file having the serenity-bdd version inside of it
            return getVersionLegacyWay();
        }
        else {
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
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty("build.number.variable")
                .orElse("BUILD_NUMBER");
    }
}
