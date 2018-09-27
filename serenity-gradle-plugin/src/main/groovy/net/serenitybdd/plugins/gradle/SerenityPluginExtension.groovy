package net.serenitybdd.plugins.gradle

import net.thucydides.core.guice.Injectors
import net.thucydides.core.webdriver.Configuration;

class SerenityPluginExtension {
    private final def configuration = Injectors.getInjector().getProvider(Configuration.class).get()
    String outputDirectory = configuration.getOutputDirectory()
    String historyDirectory = configuration.getHistoryDirectory()
    String projectKey
    String issueTrackerUrl
    String jiraUrl
    String jiraProject
    String sourceDirectory = outputDirectory
    String requirementsBaseDir
    boolean generateOutcomes
    List<String> reports
}