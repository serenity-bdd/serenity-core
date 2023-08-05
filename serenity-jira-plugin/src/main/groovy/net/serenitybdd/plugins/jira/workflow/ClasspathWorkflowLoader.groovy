package net.serenitybdd.plugins.jira.workflow

import net.thucydides.model.util.EnvironmentVariables
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ClasspathWorkflowLoader implements WorkflowLoader {

    public static final String BUNDLED_WORKFLOW = "default-workflow.groovy"
    public static final String WORKFLOW_CONFIGURATION_PROPERTY = "serenity.jira.workflow"
    public static final String ACTIVATE_WORKFLOW_PROPERTY = "serenity.jira.workflow.active"

    private static final Logger LOGGER = LoggerFactory.getLogger(ClasspathWorkflowLoader.class)

    private final String defaultWorkflow
    private final EnvironmentVariables environmentVariables

    ClasspathWorkflowLoader(String defaultWorkflow,
                            EnvironmentVariables environmentVariables) {
        this.defaultWorkflow = defaultWorkflow
        this.environmentVariables = environmentVariables
    }

    String getDefaultWorkflow() {
        return defaultWorkflow
    }

    Workflow load() {
        InputStream inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(getWorkflowFile())
        loadFrom(getWorkflowFile(), inputStream)
    }

    private Workflow loadFrom(name, configFile) {
        return new Workflow(name, configFile.text, workflowActive())
    }


    boolean workflowActive() {
        if (systemConfiguredWorkflowPropertyIsDefined() && !systemConfiguredWorkflowFileExists()) {
            false
        } else {
            (systemConfiguredWorkflowFileExists()
            || defaultWorkflowIsPresent()
            || worflowActivatedViaTheSystemProperty())
        }
    }

     boolean systemConfiguredWorkflowPropertyIsDefined() {
         getSystemConfiguredWorkflow() != null
    }

    boolean worflowActivatedViaTheSystemProperty() {
        Boolean.valueOf(environmentVariables.getProperty(ACTIVATE_WORKFLOW_PROPERTY,"false"))
    }

    def getWorkflowFile() {
        if (systemConfiguredWorkflowFileExists()) {
            systemConfiguredWorkflow
        } else if (defaultWorkflowIsPresent()) {
            defaultWorkflow
        } else {
            BUNDLED_WORKFLOW
        }
    }

    boolean defaultWorkflowIsPresent() {
        def defaultWorkflowPath = fileOnClasspathAt(defaultWorkflow)
        if (defaultWorkflowPath) {
            return new File(defaultWorkflowPath).exists()
        } else {
            return false
        }
    }

    def fileOnClasspathAt(String resource) {
        Thread.currentThread().contextClassLoader.getResource(resource)?.file
    }

    def getSystemConfiguredWorkflow() {
        return environmentVariables.getProperty(WORKFLOW_CONFIGURATION_PROPERTY)
    }

    def systemConfiguredWorkflowFileExists() {
        return (systemConfiguredWorkflow != null) && (fileOnClasspathAt(systemConfiguredWorkflow))
    }

    def getSystemConfiguredWorkflowFile() {
        if (fileOnClasspathAt(systemConfiguredWorkflow)) {
            return fileOnClasspathAt(systemConfiguredWorkflow)
        } else {
            LOGGER.error("Failed to load system-specified JIRA workflow configuration at {}", systemConfiguredWorkflow)
            return null
        }
    }

}
