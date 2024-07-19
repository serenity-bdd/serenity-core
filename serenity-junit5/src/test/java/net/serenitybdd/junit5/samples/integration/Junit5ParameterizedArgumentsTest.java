package net.serenitybdd.junit5.samples.integration;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SerenityJUnit5Extension.class)
public class Junit5ParameterizedArgumentsTest {

    @ParameterizedTest
    @ArgumentsSource(Junit5ParameterizedArgumentsTest.StringArgumentsProvider.class)
    void shouldCalculateResultsWithArgSource(String calculation) {
        System.out.println(calculation);
    }

    public static class StringArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                            "1,+,2,3",
                            "7,-,3,4",
                            "3,*,2,6",
                            "10,/,2,5")
                    .map(Arguments::of);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(Junit5ParameterizedArgumentsTest.MultiArgumentsProvider.class)
    void shouldCalculateResultsWithMultipleArgSource(String a, String op, String b, String res) {
        System.out.println(a + " " + op + " " + b + " = " + res);
    }
    public static class MultiArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                            new Object[]{"1","+","2","3"},
                            new Object[]{"7","-","3","4"}
                    )
                    .map(Arguments::of);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(Junit5ParameterizedArgumentsTest.ExtensionContextArgumentsProvider.class)
    void shouldCalculateResultsForArgumentSourceThatNeedsTheExtensionContext(String a, String op, String b, String res) {
        System.out.println(a + " " + op + " " + b + " = " + res);
    }
    public static class ExtensionContextArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            context.getDisplayName();
            return Stream.of(
                            new Object[]{"1","+","2","3"},
                            new Object[]{"7","-","3","4"}
                    )
                    .map(Arguments::of);
        }
    }

}
