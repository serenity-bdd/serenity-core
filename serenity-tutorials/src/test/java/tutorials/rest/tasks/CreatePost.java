package tutorials.rest.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * A Screenplay task to create a new post.
 */
public class CreatePost implements Task {

    private final tutorials.rest.model.Post post;

    public CreatePost(tutorials.rest.model.Post post) {
        this.post = post;
    }

    public static CreatePost withDetails(String title, String body, int userId) {
        return instrumented(CreatePost.class,
            new tutorials.rest.model.Post(title, body, userId));
    }

    public static CreatePost withTitle(String title) {
        return instrumented(CreatePost.class,
            new tutorials.rest.model.Post(title, "Default body", 1));
    }

    @Override
    @Step("{0} creates a new post titled '{1}'")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            Post.to("/posts")
                .with(request -> request
                    .contentType("application/json")
                    .body(post)
                )
        );
    }

    @Override
    public String toString() {
        return post.getTitle();
    }
}
