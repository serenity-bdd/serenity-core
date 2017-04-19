package net.serenitybdd.plugins.gradle

import net.thucydides.core.util.TestResources
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Ignore
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths
/**
 * Created by john on 9/11/2014.
 */
class WhenUsingTheGradlePlugin extends Specification {
    def "should add the aggregate task to a project"() {
        given:
            Project project = ProjectBuilder.builder().build()
        when:
            project.apply plugin: 'java'
            project.apply plugin: 'net.serenity-bdd.aggregator'
        then:
            project.tasks.aggregate
    }

    def "should add the checkOutcomes task to a project"() {
        given:
            Project project = ProjectBuilder.builder().build()
        when:
            project.apply plugin: 'java'
            project.apply plugin: 'net.serenity-bdd.aggregator'
        then:
            project.tasks.checkOutcomes
    }

    def "should add the history task to a project"() {
        given:
        Project project = ProjectBuilder.builder().build()
        when:
        project.apply plugin: 'java'
        project.apply plugin: 'net.serenity-bdd.aggregator'
        then:
        project.tasks.history
    }

    def "should add the clear history task to a project"() {
        given:
        Project project = ProjectBuilder.builder().build()
        when:
        project.apply plugin: 'java'
        project.apply plugin: 'net.serenity-bdd.aggregator'
        then:
        project.tasks.clearHistory
    }
}