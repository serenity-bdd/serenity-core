package io.cucumber.core.plugin;

import io.cucumber.core.options.*;
import io.cucumber.messages.types.Examples;
import io.cucumber.messages.types.TableRow;

import java.net.URI;
import java.util.*;

public class LineFilters {

    private final Map<URI, Set<Integer>> lineFilters;

    public LineFilters() {
        lineFilters = newLineFilters();
    }

    public static LineFilters forCurrentContext() {
        return new LineFilters();
    }

    public Optional<URI> getURIForFeaturePath(URI featurePath) {
        return lineFilters.keySet().stream()
                .filter(uri -> featurePath.equals(uri))
                .findFirst();
    }

    private Map<URI, Set<Integer>> newLineFilters() {
        RuntimeOptions runtimeOptions = RuntimeOptions.defaultOptions();
        Map<URI, Set<Integer>> lineFiltersFromRuntime = runtimeOptions.getLineFilters();
        if (lineFiltersFromRuntime == null) {
            return new HashMap<>();
        } else {
            return lineFiltersFromRuntime;
        }
    }

    public Set<Integer> getLineNumbersFor(URI featurePath) {
        return lineFilters.get(featurePath);
    }


    public boolean examplesAreNotExcluded(Examples examples, URI featurePath) {
        if (lineFilters.isEmpty()) {
            return true;
        }
        if (lineFiltersContainFeaturePath(featurePath)) {
            Optional<URI> uriForFeaturePath = getURIForFeaturePath(featurePath);
            return uriForFeaturePath.filter(
                    uri -> examples.getTableBody().stream()
                            .anyMatch(
                                    row -> lineFilters.get(uri).contains(Math.toIntExact(row.getLocation().getLine())))
            ).isPresent();
        }
        return false;
    }

    public boolean tableRowIsNotExcludedBy(TableRow tableRow, URI featurePath) {
        if (lineFilters.isEmpty()) {
            return true;
        }
        if (lineFiltersContainFeaturePath(featurePath)) {
            Optional<URI> uriForFeaturePath = getURIForFeaturePath(featurePath);
            return uriForFeaturePath.filter(uri -> lineFilters.get(uri).contains(Math.toIntExact(tableRow.getLocation().getLine()))).isPresent();
        }
        return false;
    }

    private boolean lineFiltersContainFeaturePath(URI featurePath) {
        return getURIForFeaturePath(featurePath) != null;
    }


}
