package net.serenitybdd.ant.util;

import net.serenitybdd.ant.util.PathProcessor;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenPreparingResourcePaths {

    PathProcessor pathProcessor = new PathProcessor();

    @Test
    public void shouldNotModifyUnprefixedPaths() {
        assertThat(pathProcessor.normalize("some/path")).isEqualTo("some/path");
    }




}
