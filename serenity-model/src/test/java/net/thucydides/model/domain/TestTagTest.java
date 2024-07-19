package net.thucydides.model.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TestTagTest {

    private static Stream<Arguments> browserPlatformTags() {
        return Stream.of(
                Arguments.of("color:red", false),
                Arguments.of("red", false),
                Arguments.of("red,", false),
                Arguments.of("chrome", false),
                Arguments.of("random,windows", false),
                Arguments.of("chrome,random", false),
                Arguments.of("chrome,windows", true)
        );
    }

    @ParameterizedTest
    @MethodSource("browserPlatformTags")
    void testWithStringAndBoolean(String tag, boolean isABrowserPlatformTag) {
        assertThat(TestTag.withValue(tag).isABrowserPlatformCombination()).isEqualTo(isABrowserPlatformTag);
    }
}
