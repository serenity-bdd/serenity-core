package net.serenitybdd.screenplay.rest.abiities;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenQueryingARestAPI {
    @Test
    public void queries_should_be_relative_to_the_base_url() {

        CallAnApi callAnApi = CallAnApi.at("https://reqres.in");

        callAnApi.get("/api/users");

        assertThat(callAnApi.getLastResponse().statusCode()).isEqualTo(200);
    }
}
