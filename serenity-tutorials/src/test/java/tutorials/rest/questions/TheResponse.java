package tutorials.rest.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Question;

import java.util.List;

/**
 * Generic Screenplay questions for querying REST responses.
 */
public class TheResponse {

    /**
     * Get the status code of the last response.
     */
    public static Question<Integer> statusCode() {
        return actor -> SerenityRest.lastResponse().getStatusCode();
    }

    /**
     * Get a string field from the last response using jsonPath.
     */
    public static Question<String> field(String path) {
        return actor -> SerenityRest.lastResponse()
                                    .jsonPath()
                                    .getString(path);
    }

    /**
     * Get an integer field from the last response using jsonPath.
     */
    public static Question<Integer> intField(String path) {
        return actor -> SerenityRest.lastResponse()
                                    .jsonPath()
                                    .getInt(path);
    }

    /**
     * Get a list of items from the last response using jsonPath.
     */
    public static <T> Question<List<T>> listOf(String path, Class<T> type) {
        return actor -> SerenityRest.lastResponse()
                                    .jsonPath()
                                    .getList(path, type);
    }

    /**
     * Get the count of items in a list from the last response.
     */
    public static Question<Integer> countOf(String path) {
        return actor -> SerenityRest.lastResponse()
                                    .jsonPath()
                                    .getList(path)
                                    .size();
    }

    /**
     * Get the response time in milliseconds.
     */
    public static Question<Long> timeInMillis() {
        return actor -> SerenityRest.lastResponse().getTime();
    }
}
