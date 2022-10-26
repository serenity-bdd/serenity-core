package net.thucydides.samples;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SerenityJUnit5Extension.class)
public class SampleDataDrivenTest {
    @ParameterizedTest
    @CsvFileSource(resources = "/test-data/simple-data.csv")
    void checkNames(String name, String age, String address) {
        Serenity.reportThat("Name should not be empty",
                () -> assertThat(name).isNotEmpty()
        );
    }
}
