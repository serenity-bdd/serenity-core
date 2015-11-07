package net.serenitybdd.plugins.gradle.integration

import net.serenitybdd.core.annotations.findby.By
import net.serenitybdd.plugins.gradle.SerenityPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import spock.lang.Shared
import spock.lang.Specification
import net.thucydides.core.util.TestResources
import java.nio.file.Files

/**
 * User: YamStranger
 * Date: 11/5/15
 * Time: 10:57 AM
 */
class WhenRunTestsWithGradlePlugin extends Specification {

    def "should fail tests in gradle project with 2 max.retries"() {
        given: "simple project for aggregation task with max.retries = 2"
            def testProject = TestResources
                .directoryInClasspathCalled "test-retries-gradle-project"
            def project = ProjectBuilder.builder().withProjectDir(testProject)
                .build()
            def plugin = new SerenityPlugin()
            def reports = project.projectDir.toPath()

        when: "project build and aggregation plugin called"
            project.apply plugin: 'java'
            plugin.apply(project)
            project.test.systemProperty 'max.retries', 2
            def folders = project.serenity.outputDirectory.split "\\|/"
            folders.each { reports = reports.resolve(it) }

            ["clean","compileJava", "processResources", "classes",
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
        then: "tests fail"
            project.getTasksByName("test", false).first().state.failure != null
        then: "report generated in ${project.serenity.outputDirectory} dir"
            Files.exists reports
            Files.isDirectory reports
            Files.exists reports.resolve("index.html")
    }

    def "should run tests successfully in gradle project with 3 max.retries"() {
        given: "simple project for aggregation task with max.retries = 3"
            def testProject = TestResources
                .directoryInClasspathCalled "test-retries-gradle-project"
            def project = ProjectBuilder.builder().withProjectDir(testProject)
                .build()
            def plugin = new SerenityPlugin()
            def reports = project.projectDir.toPath()

        when: "project build and aggregation plugin called"
            project.apply plugin: 'java'
            plugin.apply(project)
            project.test.systemProperty 'max.retries', 3
            def folders = project.serenity.outputDirectory.split "\\|/"
            folders.each { reports = reports.resolve(it) }

            ["clean","compileJava", "processResources", "classes",
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
        then: "tests fail"
            project.getTasksByName("test", false).first().state.failure ==null
        then: "report generated in ${project.serenity.outputDirectory} dir"
            Files.exists reports
            Files.isDirectory reports
            Files.exists reports.resolve("index.html")
    }
}