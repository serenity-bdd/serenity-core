package net.thucydides.core.reports.html;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;

public class MarkdownRendering {

    private final String DEFAULT_MARKDOWN_ON = "story,narrative,scenario";
    public enum RenderedElements {
        list, story, narrative, scenario, step
    }

    private final EnvironmentVariables environmentVariables;

    public MarkdownRendering(EnvironmentVariables environmentVariables) {

        this.environmentVariables = environmentVariables;
    }

    public static MarkdownRendering configuredIn(EnvironmentVariables environmentVariables) {
        return new MarkdownRendering(environmentVariables);
    }

    public boolean renderMarkdownFor(RenderedElements elementType) {
        return ThucydidesSystemProperty.ENABLE_MARKDOWN.from(environmentVariables,DEFAULT_MARKDOWN_ON).contains(elementType.name());
    }
}
