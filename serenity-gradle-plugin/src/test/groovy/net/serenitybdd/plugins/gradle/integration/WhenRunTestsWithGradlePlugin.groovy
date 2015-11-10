package net.serenitybdd.plugins.gradle.integration

import net.serenitybdd.plugins.gradle.SerenityPlugin
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import net.thucydides.core.util.TestResources
import java.nio.file.Files

/**
 * User: YamStranger
 * Date: 11/5/15
 * Time: 10:57 AM
 */
class WhenRunTestsWithGradlePlugin extends Specification {

    def "should fail tests in gradle project without junit.retry.tests"() {
        given: "simple project for aggregation task with max.retries = 3"
            def testProject = TestResources
                .directoryInClasspathCalled "test-retries-gradle-project"
            def project = ProjectBuilder.builder().withProjectDir(testProject)
                .build()
            def reports = project.projectDir.toPath()
            def plugin = new SerenityPlugin()
            testProject.toPath().resolve(project.buildDir.name).deleteDir()
        when: "project build and aggregation plugin called"
            project.apply plugin: 'java'
            plugin.apply(project)
            project.test.systemProperty 'max.retries', 3
            def folders = project.serenity.outputDirectory.split "\\|/"
            folders.each { reports = reports.resolve(it) }

            reports.deleteDir()

            ["compileJava", "processResources", "classes",
             "assemble", "compileTestJava", "processTestResources", "testClasses",
             "test", "aggregate"
            ].each {
                try {
                    project.getTasksByName(it, false).first().execute()
                }catch (e){
                    if(!project.gradle.startParameter.continueOnFailure){
                        throw e;
                    }
                }
            }
            def report = reports.resolve("index.html")
        then: "fail report generated in ${project.serenity.outputDirectory} dir"
            Files.exists reports
            Files.isDirectory reports
            Files.exists report
            report.text.contains("1 out of 1 tests (2 steps) failing")
    }

    def "should fail tests in gradle project with 2 max.retries"() {
        given: "simple project for aggregation task with max.retries = 2"
            def testProject = TestResources
                .directoryInClasspathCalled "test-retries-gradle-project"
            def project = ProjectBuilder.builder().withProjectDir(testProject)
                .build()
            def reports = project.projectDir.toPath()
            def plugin = new SerenityPlugin()
            testProject.toPath().resolve(project.buildDir.name).deleteDir()
        when: "project build and aggregation plugin called"
            project.apply plugin: 'java'
            plugin.apply(project)
            project.test.systemProperty 'max.retries', 2
            project.test.systemProperty 'junit.retry.tests', true
            def folders = project.serenity.outputDirectory.split "\\|/"
            folders.each { reports = reports.resolve(it) }

            reports.deleteDir()

            ["compileJava", "processResources", "classes",
             "assemble", "compileTestJava", "processTestResources", "testClasses",
             "test", "aggregate"
            ].each {
                try {
                    project.getTasksByName(it, false).first().execute()
                }catch (e){
                    if(!project.gradle.startParameter.continueOnFailure){
                        throw e;
                    }
                }
            }
            def report = reports.resolve("index.html")
        then: "fail report generated in ${project.serenity.outputDirectory} dir"
            Files.exists reports
            Files.isDirectory reports
            Files.exists report
            report.text.contains("1 out of 1 tests (2 steps) failing")
    }

    def "should run tests successfully in gradle project with 3 max.retries"() {
        given: "simple project for aggregation task with max.retries = 3"
            def testProject = TestResources
                .directoryInClasspathCalled "test-retries-gradle-project"
            def project = ProjectBuilder.builder().withProjectDir(testProject)
                .build()
            def reports = testProject.toPath()
            def plugin = new SerenityPlugin()
            testProject.toPath().resolve(project.buildDir.name).deleteDir()
        when: "project build and aggregation plugin called"
            project.apply plugin: 'java'
            plugin.apply(project)
            project.test.systemProperty 'max.retries', 3
            project.test.systemProperty 'junit.retry.tests', true
            def folders = project.serenity.outputDirectory.split "\\|/"
            folders.each { reports = reports.resolve(it) }

            reports.deleteDir()

            ["compileJava", "processResources", "classes",
             "assemble", "compileTestJava", "processTestResources", "testClasses",
             "test", "aggregate"
            ].each {
                try {
                    project.getTasksByName(it, false).first().execute()
                }catch (e){
                    if(!project.gradle.startParameter.continueOnFailure){
                        throw e;
                    }
                }
            }
            def report = reports.resolve("index.html")
        then: "successfull report generated in ${project.serenity.outputDirectory} dir"
            Files.exists reports
            Files.isDirectory reports
            Files.exists report
            report.text.contains("1 out of 1 tests (2 steps) passing")
    }
}