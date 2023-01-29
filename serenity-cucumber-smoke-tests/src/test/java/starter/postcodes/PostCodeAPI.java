package starter.postcodes;

import io.restassured.RestAssured;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Step;

public class PostCodeAPI {

    private static String LOCATION_BY_POST_CODE_AND_COUNTRY = "http://api.zippopotam.us/{country}/{postcode}";

    @Step("Get location by postcode {0} in country {1}")
    public void fetchLocationByPostCodeAndCountry(String postcode, String country) {
        SerenityRest.given()
                .pathParam("postcode", postcode)
                .pathParam("country", country)
                .get(LOCATION_BY_POST_CODE_AND_COUNTRY);
    }
}
