package tutorials.rest.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * A Screenplay task to fetch a user by ID.
 */
public class FetchUser implements Task {

    private final int userId;

    public FetchUser(int userId) {
        this.userId = userId;
    }

    public static FetchUser withId(int userId) {
        return instrumented(FetchUser.class, userId);
    }

    @Override
    @Step("{0} fetches user with ID #userId")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            Get.resource("/users/{id}")
               .with(request -> request.pathParam("id", userId))
        );
    }
}
