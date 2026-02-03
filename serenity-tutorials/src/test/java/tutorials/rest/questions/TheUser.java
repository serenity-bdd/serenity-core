package tutorials.rest.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Question;
import tutorials.rest.model.User;

/**
 * Screenplay questions for querying user data from REST responses.
 */
public class TheUser {

    /**
     * Get the full user details from the last response.
     */
    public static Question<User> details() {
        return actor -> SerenityRest.lastResponse()
                                    .as(User.class);
    }

    /**
     * Get the user's name from the last response.
     */
    public static Question<String> name() {
        return actor -> SerenityRest.lastResponse()
                                    .jsonPath()
                                    .getString("name");
    }

    /**
     * Get the user's email from the last response.
     */
    public static Question<String> email() {
        return actor -> SerenityRest.lastResponse()
                                    .jsonPath()
                                    .getString("email");
    }

    /**
     * Get the user's ID from the last response.
     */
    public static Question<Integer> id() {
        return actor -> SerenityRest.lastResponse()
                                    .jsonPath()
                                    .getInt("id");
    }
}
