package net.serenitybdd.plugins.gradle

import net.thucydides.core.reports.ResultChecker
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter
import org.gradle.api.Plugin
import org.gradle.api.Project

class SerenityPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create("serenity", SerenityPluginExtension)
        project.task('aggregate') << {
            if (!project.serenity.projectKey) {
                project.serenity.projectKey = project.name
            }
            logger.lifecycle("Generating Serenity Reports for ${project.serenity.projectKey} to directory $project.serenity.outputDirectory")
            System.properties['thucydides.project.key'] = project.serenity.projectKey
            def reporter = new HtmlAggregateStoryReporter(project.serenity.projectKey)

            reporter.outputDirectory = new File(project.serenity.outputDirectory)

            reporter.issueTrackerUrl = project.serenity.issueTrackerUrl
            reporter.jiraUrl = project.serenity.jiraUrl
            reporter.jiraProject = project.serenity.jiraProject
            reporter.generateReportsForTestResultsFrom(new File(project.projectDir, project.serenity.sourceDirectory))
        }

        project.task('checkOutcomes') << {
            def reportDir = new File(project.projectDir, project.serenity.outputDirectory)
            logger.lifecycle("Checking serenity results for ${project.serenity.projectKey} in directory $reportDir")
            def checker = new ResultChecker(reportDir)
            checker.checkTestResults()
        }

    }
}