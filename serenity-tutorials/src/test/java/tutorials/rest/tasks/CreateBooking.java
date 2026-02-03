package tutorials.rest.tasks;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.annotations.Step;
import tutorials.rest.model.Booking;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * A Screenplay task to create a booking on Restful-Booker.
 * Stores the booking ID in the actor's memory for subsequent requests.
 */
public class CreateBooking implements Task {

    private final Booking booking;

    public CreateBooking(Booking booking) {
        this.booking = booking;
    }

    public static CreateBooking forGuest(String firstname, String lastname) {
        Booking booking = new Booking(
            firstname, lastname, 150, true,
            "2024-06-01", "2024-06-05", "Breakfast"
        );
        return instrumented(CreateBooking.class, booking);
    }

    public static CreateBooking with(Booking booking) {
        return instrumented(CreateBooking.class, booking);
    }

    @Override
    @Step("{0} creates a booking for {1}")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            Post.to("/booking")
                .with(request -> request
                    .contentType("application/json")
                    .accept("application/json")
                    .body(booking)
                )
        );

        Integer bookingId = SerenityRest.lastResponse().path("bookingid");
        actor.remember("bookingId", bookingId);
    }

    @Override
    public String toString() {
        return booking.getFirstname() + " " + booking.getLastname();
    }
}
