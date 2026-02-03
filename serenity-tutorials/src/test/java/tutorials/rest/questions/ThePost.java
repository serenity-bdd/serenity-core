package tutorials.rest.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Question;
import tutorials.rest.model.Post;

/**
 * Screenplay questions for querying post data from REST responses.
 */
public class ThePost {

    /**
     * Get the full post details from the last response.
     */
    public static Question<Post> details() {
        return actor -> SerenityRest.lastResponse()
                                    .as(Post.class);
    }

    /**
     * Get the post's title from the last response.
     */
    public static Question<String> title() {
        return actor -> SerenityRest.lastResponse()
                                    .jsonPath()
                                    .getString("title");
    }

    /**
     * Get the post's ID from the last response.
     */
    public static Question<Integer> id() {
        return actor -> SerenityRest.lastResponse()
                                    .jsonPath()
                                    .getInt("id");
    }

    /**
     * Get the post's body from the last response.
     */
    public static Question<String> body() {
        return actor -> SerenityRest.lastResponse()
                                    .jsonPath()
                                    .getString("body");
    }
}
