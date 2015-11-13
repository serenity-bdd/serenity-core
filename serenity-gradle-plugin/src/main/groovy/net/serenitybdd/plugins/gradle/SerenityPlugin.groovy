package net.serenitybdd.plugins.gradle

import net.thucydides.core.reports.ResultChecker
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.nio.file.Files

class SerenityPlugin implements Plugin<Project> {

    File reportDirectory

    @Override
    void apply(Project project) {
        if(!project.extensions.findByName("serenity")) {
            project.extensions.add("serenity", new SerenityPluginExtension())
            def serenityProperties = project.rootDir.toPath().resolve("serenity.properties")
            if(Files.exists(serenityProperties)){
                def properties = new Properties()
                serenityProperties.withInputStream {
                    properties.load(it)
                }
                project.serenity.outputDirectory = properties."serenity.outputDirectory"
                project.serenity.sourceDirectory = project.serenity.outputDirectory
            }
        }

        reportDirectory = prepareReportDirectory(project)


        project.task('aggregate') {
            group 'Serenity BDD'
            description 'Generates aggregated Serenity reports'
            doLast {
                if (!project.serenity.projectKey) {
                    project.serenity.projectKey = project.name
                }
                logger.lifecycle("Generating Serenity Reports for ${project.serenity.projectKey} to directory $reportDirectory")
                System.properties['thucydides.project.key'] = project.serenity.projectKey
                def reporter = new HtmlAggregateStoryReporter(project.serenity.projectKey)

                reporter.outputDirectory = reportDirectory

                reporter.issueTrackerUrl = project.serenity.issueTrackerUrl
                reporter.jiraUrl = project.serenity.jiraUrl
                reporter.jiraProject = project.serenity.jiraProject
                reporter.generateReportsForTestResultsFrom(reportDirectory)
            }
        }

        project.task('checkOutcomes') {
            group 'Serenity BDD'
            description "Checks the Serenity reports and fails the build if there are test failures (run automatically with 'check')"

            inputs.dir reportDirectory

            doLast {
                logger.lifecycle("Checking serenity results for ${project.serenity.projectKey} in directory $reportDirectory")
                if (reportDirectory.exists()) {
                    def checker = new ResultChecker(reportDirectory)
                    checker.checkTestResults()
                }
            }
        }
        project.task('clearReports') {
            group 'Serenity BDD'
            description "Deletes the Serenity output directory (run automatically with 'clean')"

            doLast {
                reportDirectory.deleteDir()
            }
        }

        project.tasks.aggregate.mustRunAfter project.tasks.test
        project.tasks.checkOutcomes.mustRunAfter project.tasks.aggregate

        project.tasks.clean {
            it.dependsOn 'clearReports'
        }
        project.tasks.check {
            it.dependsOn 'checkOutcomes'
        }
    }

    def prepareReportDirectory(Project project) {
        new File(project.projectDir, project.serenity.outputDirectory)
    }
}