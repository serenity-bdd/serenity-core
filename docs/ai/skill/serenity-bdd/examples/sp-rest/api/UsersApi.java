package api;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.interactions.Post;

public class UsersApi {
    public static Performable fetchUser(int id) {
        return Task.where("{0} fetches user " + id, Get.resource("/users/" + id));
    }

    public static Performable createPost(String title) {
        return Task.where("{0} creates a post titled " + title,
                Post.to("/posts").with(request -> request
                        .header("Content-Type", "application/json")
                        .body("{\"title\":\"" + title + "\",\"body\":\"text\",\"userId\":1}")));
    }
}
