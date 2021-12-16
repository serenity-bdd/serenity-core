package io.cucumber.core.plugin;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FeaturePathFormatter {

    private LineFilters lineFilters;

    public FeaturePathFormatter() {
        this.lineFilters = LineFilters.forCurrentContext();
    }

    public URI featurePathWithPrefixIfNecessary(final URI featurePath) {
        return lineFilters
                .getURIForFeaturePath(featurePath)
                .map(matchingURI -> featurePathWithPrefix(matchingURI, featurePath))
                .orElse(featurePath);
    }

    private URI featurePathWithPrefix(URI featurePathUri, URI featurePath) {
        Set<Integer> allLineNumbersSet = lineFilters.getLineNumbersFor(featurePathUri);
        List<Integer> allLineNumbersList = new ArrayList<>(allLineNumbersSet);
        long featurePathPrefix = allLineNumbersList.get(0);
        URI featureURIWithPrefix = featurePathUri;
        try {
            featureURIWithPrefix =  new URI(featurePath.toString() + ":" + featurePathPrefix);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return featureURIWithPrefix;

    }

    private URI getURIForFeaturePath(Map<URI, Set<Integer>> map, URI featurePath) {
        for (URI currentURI : map.keySet()) {
            if (featurePath.equals(currentURI)) {
                return currentURI;
            }
        }
        return null;
    }
}
