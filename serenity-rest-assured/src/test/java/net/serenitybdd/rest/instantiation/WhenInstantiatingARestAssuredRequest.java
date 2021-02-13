package net.serenitybdd.rest.instantiation;

import org.junit.jupiter.api.Test;
import net.serenitybdd.rest.SerenityRest;

class WhenInstantiatingARestAssuredRequest {

    @Test
    void serenityShouldWrapTheRestAssuredResponse() {
        SerenityRest.expect();
    }
}
