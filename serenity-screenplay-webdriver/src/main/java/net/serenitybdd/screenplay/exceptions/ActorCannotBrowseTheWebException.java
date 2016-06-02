package net.serenitybdd.screenplay.exceptions;

/**
 * Created by john on 13/05/2016.
 */
public class ActorCannotBrowseTheWebException extends RuntimeException {
    public ActorCannotBrowseTheWebException(String actorName) {
        super("The actor " + actorName + " does not have the ability to browse the web");
    }
}
