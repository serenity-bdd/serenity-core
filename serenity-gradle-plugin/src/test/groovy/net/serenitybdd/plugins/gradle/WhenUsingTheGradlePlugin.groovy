package net.serenitybdd.plugins.gradle

import net.thucydides.core.util.TestResources
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Ignore
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths
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

    @Ignore('should be upgraded to use gradle 2.10 features')
    def "the 'serenity' project property is used to configure the build"() {
        given:
        Project project = ProjectBuilder.builder().build()
        when:
        project.apply plugin: 'java'
        project.apply plugin: 'net.serenity-bdd.aggregator'
        then:
        project.serenity.outputDirectory == Paths.get("").resolve('target/site/serenity').toAbsolutePath()
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

    def "should assemble aggregate report of legacy gradle project"() {
        given: "legacy project for aggregation task"
            def testProject = TestResources
                .directoryInClasspathCalled "legacy-gradle-project"
            def project = ProjectBuilder.builder().withProjectDir(testProject)
                .build()
            def plugin = new SerenityPlugin()
            def reports = project.projectDir.toPath()

        when: "project build and aggregation plugin called"
            project.apply plugin: 'java'
            plugin.apply(project)
            def folders = project.serenity.outputDirectory.split "\\|/"
            folders.each { reports = reports.resolve(it) }

            ["compileJava", "processResources", "classes",
             "assemble", "compileTestJava", "processTestResources", "testClasses",
             "test", "aggregate"
            ].each {
                project.getTasksByName(it, false).first().execute()
            }

        then: "report generated in ${project.serenity.outputDirectory} dir"
            Files.exists reports
            Files.isDirectory reports
            Files.exists reports.resolve("index.html")
    }

    def "should assemble aggregate report of gradle project"() {
        given: "simple project for aggregation task"
            def testProject = TestResources
                .directoryInClasspathCalled "simple-gradle-project"
            def project = ProjectBuilder.builder().withProjectDir(testProject)
                .build()
            def plugin = new SerenityPlugin()
            def reports = project.projectDir.toPath()

        when: "project build and aggregation plugin called"
            project.apply plugin: 'java'
            plugin.apply(project)
            def folders = project.serenity.outputDirectory.split "\\|/"
            folders.each { reports = reports.resolve(it) }


            ["compileJava", "processResources", "classes",
             "assemble", "compileTestJava", "processTestResources", "testClasses",
             "test", "aggregate"
            ].each {
                project.getTasksByName(it, false).first().execute()
            }

        then: "report generated in ${project.serenity.outputDirectory} dir"
            Files.exists reports
            Files.isDirectory reports
            Files.exists reports.resolve("index.html")
    }

    @Ignore("To review")
    def "should assemble aggregate report of gradle project in a different directory"() {
        given: "simple project for aggregation task"
            def testProject = TestResources
                    .directoryInClasspathCalled "simple-gradle-project"
            def project = ProjectBuilder.builder().withProjectDir(testProject)
                    .build()
            def plugin = new SerenityPlugin()
            def reports = project.projectDir.toPath()

        when: "project build and aggregation plugin called"
            System.setProperty("serenity.outputDirectory","different-dir")

            project.apply plugin: 'java'
            plugin.apply(project)
            def folders = project.serenity.outputDirectory.split "\\|/"
            folders.each { reports = reports.resolve(it) }

            ["compileJava", "processResources", "classes",
             "assemble", "compileTestJava", "processTestResources", "testClasses",
             "test", "aggregate"
            ].each {
                project.getTasksByName(it, false).first().execute()
            }

        then: "report generated in ${project.serenity.outputDirectory} dir"
            project.projectDir.toPath().resolve("different-dir").toFile().exists()
            System.clearProperty("serenity.outputDirectory")
    }

    @Ignore('should be upgraded to use gradle 2.10 features')
    def "should assemble aggregate report of gradle project in output dir"() {
        given: "project with customized properties for aggregation task"
            def testProject = TestResources
                .directoryInClasspathCalled "customized-gradle-project"
            def project = ProjectBuilder.builder().withProjectDir(testProject)
                .build()
            def plugin = new SerenityPlugin()
            def reports = project.projectDir.toPath()

            def properties = new Properties()
            testProject.toPath().resolve("serenity.properties").withInputStream {
                properties.load(it)
            }
            def folders = properties."serenity.outputDirectory".split "\\|/"
            folders.each { reports = reports.resolve(it) }

        when: "project build and aggregation plugin called"
            project.apply plugin: 'java'
            plugin.apply(project)
            ["compileJava", "processResources", "classes",
             "assemble", "compileTestJava", "processTestResources", "testClasses",
             "test", "aggregate", "checkOutcomes"
            ].each {
                project.getTasksByName(it, false).first().execute()
            }

        then: "report generated in ${properties."serenity.outputDirectory"} dir"
            Files.exists reports
            Files.isDirectory reports
            Files.exists reports.resolve("index.html")
    }
}