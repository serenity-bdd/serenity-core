package net.serenitybdd.plugins.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification


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

    def "the 'serenity' project property is used to configure the build"() {
        given:
        Project project = ProjectBuilder.builder().build()
        when:
        project.apply plugin: 'java'
        project.apply plugin: 'net.serenity-bdd.aggregator'
        then:
        project.serenity.outputDirectory == 'target/site/serenity'
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



}