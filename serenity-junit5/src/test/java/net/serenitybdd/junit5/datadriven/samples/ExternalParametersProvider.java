package net.serenitybdd.junit5.datadriven.samples;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ExternalParametersProvider {
	static Stream<Arguments> stringProvider() {
		return Stream.of(arguments("apple", "banana"), arguments("peaches","pears"));
	}
}
