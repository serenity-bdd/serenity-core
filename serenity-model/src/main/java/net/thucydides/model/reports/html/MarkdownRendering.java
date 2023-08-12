package net.thucydides.model.reports.html;

import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;

public class MarkdownRendering {

    private final String DEFAULT_MARKDOWN_ON = "overview,story,narrative,scenario";

    public enum RenderedElements {
        overview, story, narrative, scenario, step
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
