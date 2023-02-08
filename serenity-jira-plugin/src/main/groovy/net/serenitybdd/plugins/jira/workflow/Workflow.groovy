package net.serenitybdd.plugins.jira.workflow
/**
 * Manage JIRA workflow integration.
 * JIRA workflow integration is configured using a simple Groovy DSL to define the transitionSetMap to be performed
 * for each test result.
 */
class Workflow {

    private final String name;
    private final boolean active;

    def builder = new TransitionBuilder()

    protected Workflow(String name, String configuration, boolean active) {
        this.name = name;
        this.active = active;
        Script s = new GroovyClassLoader().parseClass(configuration).newInstance()
        s.binding = new BuilderBinding(builder:builder)
        s.run()
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public TransitionBuilder.TransitionSetMap getTransitions() {
        builder.getTransitionSetMap()
    }
}

class BuilderBinding extends Binding {
    def builder
    Object getVariable(String name) {
        return { Object... args ->  builder.invokeMethod(name,args) }
    }
}