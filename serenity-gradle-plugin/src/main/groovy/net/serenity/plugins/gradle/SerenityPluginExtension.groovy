package net.serenity.plugins.gradle

class SerenityPluginExtension {
    def String outputDirectory = 'target/site/thucydides'
    def String projectKey
    def String issueTrackerUrl
    def String jiraUrl
    def String jiraProject
    def String sourceDirectory = outputDirectory
}