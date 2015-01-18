package net.serenitybdd.plugins.gradle

class SerenityPluginExtension {
    def String outputDirectory = 'target/site/serenity'
    def String projectKey
    def String issueTrackerUrl
    def String jiraUrl
    def String jiraProject
    def String sourceDirectory = outputDirectory
}