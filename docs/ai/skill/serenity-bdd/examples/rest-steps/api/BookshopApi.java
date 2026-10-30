package api;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;

import static net.serenitybdd.rest.SerenityRest.given;

/** A step library: each @Step method is one line in the report, and holds the REST calls for it. */
public class BookshopApi {
    private String baseUri;

    public void usingTheServerAt(String baseUri) {
        this.baseUri = baseUri;
    }

    @Step("List the books in the catalogue")
    public void listTheBooks() {
        given().baseUri(baseUri).when().get("/api/books");
    }

    @Step("Look up book number {0}")
    public void lookUpBook(int id) {
        given().baseUri(baseUri).when().get("/api/books/" + id);
    }

    @Step("Place an order for book {0} as a signed-in reader")
    public void placeAnOrderFor(int bookId) {
        given().baseUri(baseUri)
                .header("Authorization", "Bearer abc123")
                .contentType("application/json")
                .body("{\"items\":[{\"bookId\":" + bookId + ",\"qty\":2}],\"name\":\"Ada\"}")
                .when().post("/api/orders");
    }

    @Step("Place an order without signing in")
    public void placeAnOrderWithoutSigningIn() {
        given().baseUri(baseUri)
                .contentType("application/json")
                .body("{\"items\":[{\"bookId\":1,\"qty\":1}],\"name\":\"Ada\"}")
                .when().post("/api/orders");
    }

    @Step("The response should have status {0}")
    public void responseShouldHaveStatus(int status) {
        SerenityRest.then().statusCode(status);
    }

    @Step("The response should list {0} books")
    public void responseShouldListBooks(int count) {
        SerenityRest.then().body("size()", org.hamcrest.Matchers.equalTo(count));
    }

    @Step("The response should show the title {0}")
    public void responseShouldShowTitle(String title) {
        SerenityRest.then().body("title", org.hamcrest.Matchers.equalTo(title));
    }

    @Step("The response should show the error {0}")
    public void responseShouldShowError(String message) {
        SerenityRest.then().body("error", org.hamcrest.Matchers.equalTo(message));
    }

    @Step("The order total should be {0}")
    public void orderTotalShouldBe(double total) {
        SerenityRest.then().body("total", org.hamcrest.Matchers.equalTo((float) total));
    }
}
