package net.thucydides.model.configuration

import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.util.EnvironmentVariables
import org.apache.commons.lang3.SystemUtils
import spock.lang.Specification

import java.nio.file.Paths

class WhenConfiguringTheOutputDirectory extends Specification {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

    def setup() {
        environmentVariables = new MockEnvironmentVariables()
    }

    def "the default output directory is target/site/serenity"() {
        when:
        def config = new SystemPropertiesConfiguration(environmentVariables);
        then:
        config.getOutputDirectory().toPath() == Paths.get("target/site/serenity")
    }

    def "the default output directory can be overridden"() {
        given:
        environmentVariables.setProperty("serenity.outputDirectory","/my/target")
        when:
        def config = new SystemPropertiesConfiguration(environmentVariables);
        then:
        config.getOutputDirectory().toPath() == Paths.get("/my/target")
    }


    def "for a maven project the default output directory should be based on the project directory"() {
        given:
        environmentVariables.setProperty("project.build.directory","/my/maven/project")
        when:
        def config = new SystemPropertiesConfiguration(environmentVariables);
        then:
        config.getOutputDirectory().toPath() == Paths.get("/my/maven/project/target/site/serenity")
    }

    def "for a maven project an overridden absolute output directory should be absolute"() {
        given:
        String absolutePath = (SystemUtils.IS_OS_WINDOWS)? "C:\\my\\target" : "/my/target";

        environmentVariables.setProperty("project.build.directory","/my/maven/project")
        environmentVariables.setProperty("serenity.outputDirectory",absolutePath)
        when:
        def config = new SystemPropertiesConfiguration(environmentVariables);
        then:

        config.getOutputDirectory().toPath() == Paths.get(absolutePath)
    }

    def "the default Maven output directory is target/site/serenity"() {
        when:
        def config = new SystemPropertiesConfiguration(environmentVariables);
        then:
        config.getOutputDirectory().toPath() == Paths.get("target/site/serenity")
    }

    def "the default Maven output directory can be overridden with project.build.directory"() {
        given:
        environmentVariables.setProperty("project.build.directory","/my/maven/project")
        when:
        def config = new SystemPropertiesConfiguration(environmentVariables);
        then:
        config.getOutputDirectory().toPath() == Paths.get("/my/maven/project/target/site/serenity")
    }

    def "the default Maven output directory can be overridden with project.reporting.OutputDirectory"() {
        given:
        environmentVariables.setProperty("project.reporting.OutputDirectory","/my/maven/project/reports")
        when:
        def config = new SystemPropertiesConfiguration(environmentVariables);
        then:
        config.getOutputDirectory().toPath() == Paths.get("/my/maven/project/reports/serenity")
    }

}
