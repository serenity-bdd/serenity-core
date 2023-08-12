package net.thucydides.model.configuration;

import net.serenitybdd.model.strings.Joiner;

import java.io.File;

class MavenBuildDirectory {

    static BuildDirectoryProvider forAMavenProject() {
        return new BuildDirectoryProvider() {
            @Override
            public String buildDirectoryFrom(String baseDirectory) {
                return Joiner.on(File.separator).join(baseDirectory, "target", "site", "serenity");
            }
        };
    }

    static BuildDirectoryProvider forAMavenProjectWithAConfiguredReportDirectoryTarget() {
        return new BuildDirectoryProvider() {
            @Override
            public String buildDirectoryFrom(String baseDirectory) {
                return Joiner.on(File.separator).join(baseDirectory, "serenity");
            }
        };
    }

    static BuildDirectoryProvider forAMavenProjectWithAConfiguredReportDirectory() {
        return new BuildDirectoryProvider() {
            @Override
            public String buildDirectoryFrom(String baseDirectory) {
                return baseDirectory;
            }
        };
    }

    static BuildDirectoryProvider forADefaultMavenConfiguration() {
        return new BuildDirectoryProvider() {
            @Override
            public String buildDirectoryFrom(String baseDirectory) {
                return Joiner.on(File.separator).join("target","site","serenity");
            }
        };
    }
}
