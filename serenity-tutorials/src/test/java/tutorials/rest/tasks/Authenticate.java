package tutorials.rest.tasks;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * A Screenplay task to authenticate with Restful-Booker API.
 * Stores the auth token in the actor's memory for subsequent requests.
 */
public class Authenticate implements Task {

    private final String username;
    private final String password;

    public Authenticate(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Authenticate withCredentials(String username, String password) {
        return instrumented(Authenticate.class, username, password);
    }

    /**
     * Authenticate with the default admin credentials for Restful-Booker.
     */
    public static Authenticate asAdmin() {
        return instrumented(Authenticate.class, "admin", "password123");
    }

    @Override
    @Step("{0} authenticates as #username")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            Post.to("/auth")
                .with(request -> request
                    .contentType("application/json")
                    .body(String.format("""
                        {"username": "%s", "password": "%s"}
                        """, username, password))
                )
        );

        String token = SerenityRest.lastResponse().path("token");
        actor.remember("authToken", token);
    }
}
