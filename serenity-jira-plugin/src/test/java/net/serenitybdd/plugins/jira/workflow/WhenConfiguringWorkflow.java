package net.serenitybdd.plugins.jira.workflow;

import net.thucydides.model.domain.TestResult;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenConfiguringWorkflow {

    @Mock
    EnvironmentVariables environmentVariables;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_be_able_to_load_a_workflow_configuration() {

        WorkflowLoader loader = new ClasspathWorkflowLoader("default-workflow.groovy", environmentVariables);

        Workflow workflow = loader.load();
        assertThat(workflow).isNotNull();

        List<String> transitionSetMap = workflow.getTransitions()
                .forTestResult(TestResult.SUCCESS)
                .whenIssueIs("In Progress");

        assertThat(transitionSetMap).hasSize(2);
        assertThat(transitionSetMap).contains("Stop Progress", "Resolve Issue");

    }


    @Test
    public void should_be_able_to_configure_a_simple_transition() {

        Workflow workflow = new Workflow("testflow",
                " when 'Open', {\n" +
                        "    'success' should: 'Resolve issue'\n" +
                        " }", true);

        assertThat(workflow).isNotNull();

        List<String> transitionSetMap = workflow.getTransitions()
                .forTestResult(TestResult.SUCCESS)
                .whenIssueIs("Open");

        assertThat(transitionSetMap).containsExactly("Resolve issue");

    }

    @Test
    public void should_be_able_to_configure_multiple_transitions() {

        Workflow workflow = new Workflow("testflow",
                "             when 'Open', {\n" +
                        "                'success' should: 'Resolve issue'\n" +
                        "            }\n" +
                        "\n" +
                        "            when 'Resolved', {\n" +
                        "                'failure' should: 'Reopen issue'\n" +
                        "            }\n" +
                        "\n" +
                        "            when 'In Progress', {\n" +
                        "                'success' should: ['Stop Progress','Resolve issue']\n" +
                        "            }", true);

        assertThat(workflow).isNotNull();

        List<String> transitionSetMap = workflow.getTransitions()
                .forTestResult(TestResult.SUCCESS)
                .whenIssueIs("In Progress");
        assertThat(transitionSetMap).containsExactly("Stop Progress", "Resolve issue");

    }

}
