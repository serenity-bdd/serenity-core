package net.serenitybdd.rest.filters;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenMaskingBlacklistedHeaders {

    BlacklistFilter blacklistFilter;

    @Before
    public void setupFilter() {
        Set<String> blacklist = new HashSet<>();
        blacklist.add("api-key");
        blacklistFilter = new BlacklistFilter(blacklist);
    }

    @Test
    public void shouldMaskSpecifiedHeaders() {
        String filtered = blacklistFilter.filter("api-key=my-api-key-value\n  other=value\n   Accept=*/*");

        assertThat(filtered).isEqualTo("api-key=****\n  other=value\n   Accept=*/*");
    }

    @Test
    public void shouldNotModifyUnmaskedHeaders() {
        String filtered = blacklistFilter.filter("other=value\n   Accept=*/*");

        assertThat(filtered).isEqualTo("other=value\n   Accept=*/*");
    }

}
