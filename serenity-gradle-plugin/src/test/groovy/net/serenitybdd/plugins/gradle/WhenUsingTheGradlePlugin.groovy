package net.serenitybdd.plugins.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import net.thucydides.core.util.TestResources
import java.nio.file.Files


/**
 * Created by john on 9/11/2014.
 */
class WhenUsingTheGradlePlugin extends Specification {

    def "should add the aggregate task to a project"() {
        given:
            Project project = ProjectBuilder.builder().build()
        when:
            project.apply plugin: 'java'
            project.apply plugin: 'net.serenity-bdd.aggregator'
        then:
            project.tasks.aggregate
    }

    def "the 'serenity' project property is used to configure the build"() {
        given:
        Project project = ProjectBuilder.builder().build()
        when:
        project.apply plugin: 'java'
        project.apply plugin: 'net.serenity-bdd.aggregator'
        then:
        project.serenity.outputDirectory == 'target/site/serenity'
    }

    def "should add the checkOutcomes task to a project"() {
        given:
        Project project = ProjectBuilder.builder().build()
        when:
        project.apply plugin: 'java'
        project.apply plugin: 'net.serenity-bdd.aggregator'
        then:
        project.tasks.checkOutcomes
    }

    def "should build aggregate build of simple project with defsults"() {
        given:
            def testProject = TestResources
                .directoryInClasspathCalled("simple-gradle-project")
            def project = ProjectBuilder.builder().withProjectDir(testProject)
                .build()
            def plugin = new SerenityPlugin()
            def reports = project.projectDir.toPath().resolve("target")
                .resolve("site").resolve("serenity")
        when:
            project.apply plugin: 'java'
            plugin.apply(project)
            project.getTasksByName("clearReports", false).first().execute()
            project.getTasksByName("clean", false).first().execute()
            project.getTasksByName("compileJava", false).first().execute()
            project.getTasksByName("processResources", false).first().execute()
            project.getTasksByName("classes", false).first().execute()
            project.getTasksByName("assemble", false).first().execute()
            project.getTasksByName("compileTestJava", false).first().execute()
            project.getTasksByName("processTestResources", false).first().execute()
            project.getTasksByName("testClasses", false).first().execute()
            project.getTasksByName("test", false).first().execute()
            project.getTasksByName("aggregate", false).first().execute()
        then:
            Files.exists(reports)
            Files.isDirectory(reports)
    }
}