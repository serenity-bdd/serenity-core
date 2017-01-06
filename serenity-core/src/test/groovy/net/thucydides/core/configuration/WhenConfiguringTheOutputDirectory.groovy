package net.thucydides.core.configuration

import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

import java.nio.file.Paths

class WhenConfiguringTheOutputDirectory extends Specification {

    def EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

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
            environmentVariables.setProperty("project.build.directory","/my/maven/project")
            environmentVariables.setProperty("serenity.outputDirectory","/my/target")
        when:
            def config = new SystemPropertiesConfiguration(environmentVariables);
        then:

            config.getOutputDirectory().toPath() == Paths.get("/my/target")
    }

    def "for a maven project an overridden relative output directory should be relative to the maven project directory"() {
        given:
            environmentVariables.setProperty("project.build.directory","/my/maven/multimodule/project")
            environmentVariables.setProperty("serenity.outputDirectory","../my/target")
        when:
            def config = new SystemPropertiesConfiguration(environmentVariables);
        then:
            config.getOutputDirectory().toPath() == Paths.get("/my/maven/multimodule/project/../my/target")
    }

    def "the default Maven output directory is target/site/serenity"() {
        expect:
            Paths.get(MavenOrGradleBuildPath.specifiedIn(environmentVariables).buildDirectory) == Paths.get("target/site/serenity")
    }

    def "the default Maven output directory can be overridden with project.build.directory"() {
        when:
            environmentVariables.setProperty("project.build.directory","/my/maven/project")
        then:
            Paths.get(MavenOrGradleBuildPath.specifiedIn(environmentVariables).buildDirectory) == Paths.get("/my/maven/project/target/site/serenity")
    }

    def "the default Maven output directory can be overridden with project.reporting.OutputDirectory"() {
        when:
            environmentVariables.setProperty("project.reporting.OutputDirectory","/my/maven/project/reports")
        then:
            Paths.get(MavenOrGradleBuildPath.specifiedIn(environmentVariables).buildDirectory) == Paths.get("/my/maven/project/reports/serenity")
    }

}