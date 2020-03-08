package net.thucydides.core.requirements.model.cucumber;

import io.cucumber.core.resource.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class URIResource implements Resource
{
    private final Path resource;

    public URIResource(Path resource)
    {
        this.resource = resource;
    }

    @Override
    public URI getUri()
    {
        return resource.toUri();
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        return Files.newInputStream(resource);
    }
}
