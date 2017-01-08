package net.serenitybdd.plugins.gradle

import net.thucydides.core.guice.Injectors
import net.thucydides.core.reports.ResultChecker
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter
import net.thucydides.core.webdriver.Configuration
import net.thucydides.core.configuration.SystemPropertiesConfiguration
import org.gradle.api.Plugin
import org.gradle.api.Project

class SerenityPlugin implements Plugin<Project> {

    File reportDirectory

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

    def updateSystemPath(Project project) {
        System.properties['project.build.directory'] = project.projectDir.getAbsolutePath()
        println ("Updating project.build.directory to ${project.projectDir.getAbsolutePath()}")
        def SystemPropertiesConfiguration configuration = (SystemPropertiesConfiguration) Injectors.getInjector().getProvider(Configuration.class).get()
        configuration.getEnvironmentVariables().setProperty('project.build.directory', project.projectDir.getAbsolutePath())
        configuration.reloadOutputDirectory()
    }


    def updateProperties(Project project) {
        updateSystemPath(project)
        def config = Injectors.getInjector().getProvider(Configuration.class).get()
        println ("Updating project.serenity.outputDirectory to ${config.getOutputDirectory()}")
        project.serenity.outputDirectory = config.getOutputDirectory()
        project.serenity.sourceDirectory = config.getOutputDirectory()
    }

}
