package net.thucydides.ant.util;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class WhenPreparingResourcePaths {

    PathProcessor pathProcessor = new PathProcessor();

    @Test
    public void shouldNotModifyUnprefixedPaths() {
        assertThat(pathProcessor.normalize("some/path")).isEqualTo("some/path");
    }

}
