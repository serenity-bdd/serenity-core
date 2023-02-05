package net.serenitybdd.plugins.jira.domain;

import java.net.URI;

public class Version {
    private final URI self;
    private final Long id;
    private final String name;
    private final boolean archived;
    private final boolean released;

    public Version(URI self, Long id, String name, boolean archived, boolean released) {
        this.self = self;
        this.id = id;
        this.name = name;
        this.archived = archived;
        this.released = released;
    }

    public URI getSelf() {
        return self;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isArchived() {
        return archived;
    }

    public boolean isReleased() {
        return released;
    }
}
