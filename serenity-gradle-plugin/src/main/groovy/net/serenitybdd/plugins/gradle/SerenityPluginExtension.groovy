package net.serenitybdd.plugins.gradle

import net.thucydides.core.guice.Injectors
import net.thucydides.core.webdriver.Configuration;

class SerenityPluginExtension {
    private final def configuration = Injectors.getInjector().getProvider(Configuration.class).get()
    def String outputDirectory = configuration.getOutputDirectory()
    def String historyDirectory = configuration.getHistoryDirectory()
    def String projectKey
    def String issueTrackerUrl
    def String jiraUrl
    def String jiraProject
    def String sourceDirectory = outputDirectory
}