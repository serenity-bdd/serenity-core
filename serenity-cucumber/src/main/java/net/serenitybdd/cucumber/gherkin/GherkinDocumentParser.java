package net.serenitybdd.cucumber.gherkin;

import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.Envelope;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.messages.types.ParseError;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class GherkinDocumentParser {
    public static GherkinDocument getDocument(io.cucumber.core.gherkin.Feature feature) {
        GherkinParser parser = GherkinParser.builder()
                .includeSource(false)
                .includePickles(false)
                .build();

        List<Envelope> parsed = null;
        try {
            Path path = fromUri(feature.getUri());
            parsed = parser.parse(path).toList();
        } catch (IOException | URISyntaxException e) {
            throw new InvalidGherkinException(e.getMessage());
        }

        Optional<GherkinDocument> gherkinDocument = parsed.stream()
                .map(Envelope::getGherkinDocument)
                .flatMap(Optional::stream)
                .findFirst();

        List<ParseError> parseErrors = parsed.stream()
                .map(Envelope::getParseError)
                .flatMap(Optional::stream)
                .toList();

        return gherkinDocument.orElseThrow(() -> new InvalidGherkinException("Could not parse feature file " + feature.getUri() + "\n" + parseErrors));
    }

    private static Path fromUri(URI uri) throws URISyntaxException {
        String uriPath = uri.toString();
        if (uriPath.startsWith("classpath:")) {
            String resourcePath = uriPath.substring(uriPath.indexOf(":") + 1);
            URL resourceUrl = Thread.currentThread()
                    .getContextClassLoader()
                    .getResource(resourcePath);

            if (resourceUrl == null) {
                throw new IllegalArgumentException("Feature file not found on: " + resourcePath);
            }
            return Paths.get(resourceUrl.toURI());
        } else if (uriPath.startsWith("file:")) {
            return Paths.get(uriPath.substring(uriPath.indexOf(":") + 1));
        }
        return Paths.get(uriPath);
    }
}
