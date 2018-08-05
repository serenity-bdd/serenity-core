package net.serenitybdd.plugins.gradle

import net.serenitybdd.core.history.FileSystemTestOutcomeSummaryRecorder
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.guice.Injectors
import net.thucydides.core.reports.ExtendedReports
import net.thucydides.core.reports.ResultChecker
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter
import net.thucydides.core.webdriver.Configuration
import net.thucydides.core.configuration.SystemPropertiesConfiguration
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SerenityPlugin implements Plugin<Project> {

    Path reportDirectory
    Path historyDirectory

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    void apply(Project project) {
        updateSystemPath(project)
        project.extensions.create("serenity", SerenityPluginExtension)
        project.task('aggregate') {
            group = 'Serenity BDD'
            description = 'Generates aggregated Serenity reports'
            doLast {
                updateProperties(project)
                reportDirectory = prepareReportDirectory(project)
                if (!project.serenity.projectKey) {
                    project.serenity.projectKey = project.name
                }
                logger.lifecycle("Generating Serenity Reports for ${project.serenity.projectKey} to directory $reportDirectory")
                System.properties['serenity.project.key'] = project.serenity.projectKey
                System.properties['serenity.test.requirements.basedir'] = project.serenity.requirementsBaseDir
                def reporter = new HtmlAggregateStoryReporter(project.serenity.projectKey)

                reporter.outputDirectory = reportDirectory.toFile()

                reporter.issueTrackerUrl = project.serenity.issueTrackerUrl
                reporter.jiraUrl = project.serenity.jiraUrl
                reporter.jiraProject = project.serenity.jiraProject

                if (project.serenity.generateOutcomes) {
                    reporter.setGenerateTestOutcomeReports();
                }
                reporter.generateReportsForTestResultsFrom(reportDirectory.toFile())

                List<String> extendedReportTypes =  System.properties.getProperty('serenity.reports').tokenize(", ")
                ExtendedReports.named(extendedReportTypes).forEach {
                    report -> report.generateReportFrom(reportDirectory)
                }
            }
        }

        project.task('checkOutcomes') {
            group = 'Serenity BDD'
            description = "Checks the Serenity reports and fails the build if there are test failures (run automatically with 'check')"

            reportDirectory = prepareReportDirectory(project)

            log.info("SerenityPlugin:checkOutcomes: reportDirectory = ${reportDirectory}")

            inputs.files reportDirectory

            doLast {
                updateProperties(project)
                logger.lifecycle("Checking serenity results for ${project.serenity.projectKey} in directory $reportDirectory")
                if (reportDirectory.toFile().exists()) {
                    def checker = new ResultChecker(reportDirectory.toFile())
                    checker.checkTestResults()
                }
            }
        }
        project.task('clearReports') {
            group = 'Serenity BDD'
            description = "Deletes the Serenity output directory (run automatically with 'clean')"

            doLast {
                updateProperties(project)
                reportDirectory = prepareReportDirectory(project)
                FileUtils.deleteDirectory(reportDirectory.toFile())
            }
        }

        project.task('clearHistory') {
            group = 'Serenity BDD'
            description = "Deletes the Serenity history directory"

            doLast {
                updateProperties(project)
                historyDirectory = prepareHistoryDirectory(project)
                Files.delete(historyDirectory)
            }
        }

        project.task('history') {
            group = 'Serenity BDD'
            description = "Records a summary of test outcomes to be used for comparison in the next test run"

            doLast {
                updateProperties(project)
                historyDirectory = prepareHistoryDirectory(project)

                new FileSystemTestOutcomeSummaryRecorder(historyDirectory,
                                                         deletePreviousHistory())
                        .recordOutcomeSummariesFrom(project.serenity.sourceDirectory);

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

    static Path prepareReportDirectory(Project project) {
        Path outputDir = Paths.get(project.serenity.outputDirectory)
        if (!outputDir.isAbsolute()) {
            outputDir = project.projectDir.toPath().resolve(outputDir)
        }
        return outputDir
    }

    static Path prepareHistoryDirectory(Project project) {
        def outputDir = Paths.get(project.serenity.historyDirectory)
        if (!outputDir.isAbsolute()) {
            outputDir = project.projectDir.toPath().resolve(outputDir)
        }
        return outputDir
    }

    static def updateSystemPath(Project project) {
        System.properties['project.build.directory'] = project.projectDir.getAbsolutePath()
        SystemPropertiesConfiguration configuration = (SystemPropertiesConfiguration) Injectors.getInjector().getProvider(Configuration.class).get()
        configuration.getEnvironmentVariables().setProperty('project.build.directory', project.projectDir.getAbsolutePath())
        configuration.reloadOutputDirectory()
    }

    static Boolean deletePreviousHistory() {
        SystemPropertiesConfiguration configuration = (SystemPropertiesConfiguration) Injectors.getInjector().getProvider(Configuration.class).get()
        return configuration.environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.DELETE_HISTORY_DIRECTORY, true)
    }


    static def updateProperties(Project project) {
        updateSystemPath(project)
        def config = Injectors.getInjector().getProvider(Configuration.class).get()
        project.serenity.outputDirectory = config.getOutputDirectory().toPath()
        project.serenity.sourceDirectory = config.getOutputDirectory().toPath()
    }

}
