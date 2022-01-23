package net.serenitybdd.rest.filters;

import java.util.Set;

public class BlacklistFilter {
    private final Set<String> blacklistedHeaders;

    public BlacklistFilter(Set<String> blacklistedHeaders) {
        this.blacklistedHeaders = blacklistedHeaders;
    }

    public String filter(String headers) {
        StringBuilder filteredHeaders = new StringBuilder();
        for(String headerEntry : headers.split("\n")) {
            if (isBlacklisted(headerEntry.trim())) {
                filteredHeaders.append(masked(headerEntry)).append("\n");
            } else {
                filteredHeaders.append(headerEntry).append("\n");
            }
        }
        return filteredHeaders.toString().trim();
    }

    private String masked(String headerEntry) {
        int headerSize = headerEntry.indexOf("=");
        return headerEntry.substring(0, headerSize + 1) + "****";
    }

    private boolean isBlacklisted(String headerEntry) {
        return blacklistedHeaders.stream().anyMatch(
                header -> headerEntry.startsWith(header + "=")
        );
    }
}
