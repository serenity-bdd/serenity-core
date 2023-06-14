package net.serenitybdd.junit5.samples.integration.displayName;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.junit5.samples.integration.displayName.steps.TestExample;
import net.thucydides.core.annotations.Steps;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(SerenityJUnit5Extension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class JUnit5DisplayNameExample {

    @Steps
    TestExample testExample;


    @Test
    void test_spaces_ok() {
    }

    @Test
    void test_spaces_fail() {
        testExample.test_spaces_fail();
    }
}