package net.serenitybdd.screenplay;

/**
 * = Task
 *
 * It is common to add builder methods to the Task class, in order to make the tests read more fluently. For example:
 *
 * [source,java]
 * --
 * purchase().anApple().thatCosts(0).dollars()
 * --
 *
 */
public interface Performable {
    <T extends Actor> void performAs(T actor);
}
