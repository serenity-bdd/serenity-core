package net.serenitybdd.plugins.gradle

import net.serenitybdd.core.history.FileSystemTestOutcomeSummaryRecorder
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.guice.Injectors
import net.thucydides.core.reports.ResultChecker
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter
import net.thucydides.core.webdriver.Configuration
import net.thucydides.core.configuration.SystemPropertiesConfiguration
import org.gradle.api.Plugin
import org.gradle.api.Project

class SerenityPlugin implements Plugin<Project> {

    File reportDirectory
    File historyDirectory

    @Override
    void apply(Project project) {
        updateSystemPath(project)
        project.extensions.create("serenity", SerenityPluginExtension)
        project.task('aggregate') {
            group 'Serenity BDD'
            description 'Generates aggregated Serenity reports'
            doLast {
                updateProperties(project)
                reportDirectory = prepareReportDirectory(project)
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
                updateProperties(project)
                reportDirectory = prepareReportDirectory(project)
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
                updateProperties(project)
                reportDirectory = prepareReportDirectory(project)
                reportDirectory.deleteDir()
            }
        }

        project.task('clearHistory') {
            group 'Serenity BDD'
            description "Deletes the Serenity history directory"

            doLast {
                updateProperties(project)
                historyDirectory = prepareHistoryDirectory(project)
                historyDirectory.deleteDir()
            }
        }

        project.task('history') {
            group 'Serenity BDD'
            description "Records a summary of test outcomes to be used for comparison in the next test run"

            doLast {
                updateProperties(project)
                historyDirectory = prepareHistoryDirectory(project)

                new FileSystemTestOutcomeSummaryRecorder(historyDirectory, deletePreviousHistory()).recordOutcomeSummariesFrom(project.serenity.sourceDirectory);

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
        def outputDir = new File(project.serenity.outputDirectory)
        if (!outputDir.isAbsolute()) {
            outputDir = project.projectDir.toPath().resolve(outputDir.toPath()).toFile()
        }
        outputDir
    }

    def prepareHistoryDirectory(Project project) {
        def outputDir = new File(project.serenity.historyDirectory)
        if (!outputDir.isAbsolute()) {
            outputDir = project.projectDir.toPath().resolve(outputDir.toPath()).toFile()
        }
        outputDir
    }

    def updateSystemPath(Project project) {
        System.properties['project.build.directory'] = project.projectDir.getAbsolutePath()
        println ("Updating project.build.directory to ${project.projectDir.getAbsolutePath()}")
        def SystemPropertiesConfiguration configuration = (SystemPropertiesConfiguration) Injectors.getInjector().getProvider(Configuration.class).get()
        configuration.getEnvironmentVariables().setProperty('project.build.directory', project.projectDir.getAbsolutePath())
        configuration.reloadOutputDirectory()
    }

    def deletePreviousHistory() {
        def SystemPropertiesConfiguration configuration = (SystemPropertiesConfiguration) Injectors.getInjector().getProvider(Configuration.class).get()
        configuration.environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.DELETE_HISTORY_DIRECTORY, true)
    }


    def updateProperties(Project project) {
        updateSystemPath(project)
        def config = Injectors.getInjector().getProvider(Configuration.class).get()
        println ("Updating project.serenity.outputDirectory to ${config.getOutputDirectory()}")
        project.serenity.outputDirectory = config.getOutputDirectory()
        project.serenity.sourceDirectory = config.getOutputDirectory()
    }

}
