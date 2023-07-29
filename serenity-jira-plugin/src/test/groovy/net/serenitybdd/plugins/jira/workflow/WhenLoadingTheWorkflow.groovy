package net.serenitybdd.plugins.jira.workflow

import net.thucydides.core.util.EnvironmentVariables
import spock.lang.Specification

class WhenLoadingTheWorkflow extends Specification {

    def EnvironmentVariables environmentVariables = Mock();

    def "should look for the jira-workflow.groovy configuration file by default"() {
        when:
        def workflowLoader = new ClasspathWorkflowLoader('jira-workflow.groovy', environmentVariables)

        then:
        workflowLoader.defaultWorkflow == 'jira-workflow.groovy'
    }

    def "should try to load the system-defined workflow configuration if provided"() {
        given:
        environmentVariables.getProperty(ClasspathWorkflowLoader.WORKFLOW_CONFIGURATION_PROPERTY) >> 'custom-workflow.groovy'
        when:
        ClasspathWorkflowLoader workflowLoader = new ClasspathWorkflowLoader('workflow-by-default.groovy', environmentVariables)
        Workflow workflow = workflowLoader.load()
        then:
        workflow.name == 'custom-workflow.groovy'
    }

    def "should load the default workflow if available and no system property is set"() {
        given:
        ClasspathWorkflowLoader workflowLoader = new ClasspathWorkflowLoader('workflow-by-default.groovy', environmentVariables)
        when:
        Workflow workflow = workflowLoader.load()
        then:
        workflow.name == 'workflow-by-default.groovy'
    }

    def "the workflow specified by the system property should override the default workflow"() {
        given:
        ClasspathWorkflowLoader workflowLoader = new ClasspathWorkflowLoader('workflow-by-default.groovy', environmentVariables)
        and:
        environmentVariables.getProperty(ClasspathWorkflowLoader.WORKFLOW_CONFIGURATION_PROPERTY) >> 'custom-workflow.groovy'
        when:
        Workflow workflow = workflowLoader.load()
        then:
        workflow.name == 'custom-workflow.groovy'
    }

    def "when the system-specified workflow does not exist default to the convention-based workflow"() {
        given:
        ClasspathWorkflowLoader workflowLoader = new ClasspathWorkflowLoader('workflow-by-default.groovy', environmentVariables)
        and:
        environmentVariables.getProperty(ClasspathWorkflowLoader.WORKFLOW_CONFIGURATION_PROPERTY) >> 'does-not-exist-workflow.groovy'
        when:
        Workflow workflow = workflowLoader.load()
        then:
        workflow.name == 'workflow-by-default.groovy'
    }

    def "when the system-specified and the convention-based workflows do not exist default to the bundledworkflow"() {
        given:
        ClasspathWorkflowLoader workflowLoader = new ClasspathWorkflowLoader('default-workflow-does-not-exist.groovy', environmentVariables)
        and:
        environmentVariables.getProperty(ClasspathWorkflowLoader.WORKFLOW_CONFIGURATION_PROPERTY) >> 'does-not-exist-workflow.groovy'
        when:
        Workflow workflow = workflowLoader.load()
        then:
        workflow.name == 'default-workflow.groovy'
    }


    def "should use the default workflow if no jira-workflow.groovy file is found"() {
        given:
        ClasspathWorkflowLoader workflowLoader = new ClasspathWorkflowLoader('jira-workflow.groovy', environmentVariables)
        when:
        Workflow workflow = workflowLoader.load()
        then:
        workflow.name == 'default-workflow.groovy'
    }

    def "should activate workflow updates if the workflow is defined by the system property and exists"() {
        given:
        ClasspathWorkflowLoader workflowLoader = new ClasspathWorkflowLoader('workflow-by-default.groovy', environmentVariables)
        and:
        environmentVariables.getProperty(ClasspathWorkflowLoader.WORKFLOW_CONFIGURATION_PROPERTY) >> 'custom-workflow.groovy'
        when:
        Workflow workflow = workflowLoader.load()
        then:
        workflow.active == true
    }

    def "should not activate workflow updates if the workflow is defined by the system property but does not exist"() {
        given:
        ClasspathWorkflowLoader workflowLoader = new ClasspathWorkflowLoader('workflow-by-default.groovy', environmentVariables)
        and:
        environmentVariables.getProperty(ClasspathWorkflowLoader.WORKFLOW_CONFIGURATION_PROPERTY) >> 'workflow-that-does-not-exist.groovy'
        when:
        Workflow workflow = workflowLoader.load()
        then:
        workflow.active == false
    }


    def "should activate workflow updates if the convention-based workflow file exists"() {
        given:
        ClasspathWorkflowLoader workflowLoader = new ClasspathWorkflowLoader('workflow-by-default.groovy', environmentVariables)
        when:
        Workflow workflow = workflowLoader.load()
        then:
        workflow.active == true
    }

    def "should not activate workflow updates if the convention-based workflow file does not exist"() {
        given:
        ClasspathWorkflowLoader workflowLoader = new ClasspathWorkflowLoader('does-not-exist.groovy', environmentVariables)
        when:
        Workflow workflow = workflowLoader.load()
        then:
        workflow.active == false
    }

    def "should activate default workflow if the corresponding system property is set"() {
        given:
        environmentVariables.getProperty(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY) >> 'true'
        ClasspathWorkflowLoader workflowLoader = new ClasspathWorkflowLoader('default-workflow.groovy', environmentVariables)
        when:
        Workflow workflow = workflowLoader.load()
        then:
        workflow.name == 'default-workflow.groovy'
        and:
        workflow.active == true
    }

}
